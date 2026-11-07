const { pool } = require('../config/database');
const ReviewModel = require('../models/review.model');
const BookingModel = require('../models/booking.model');
const HomestayModel = require('../models/homestay.model');
const ApiError = require('../utils/ApiError');
const { getPaginationParams, buildPaginationMeta } = require('../utils/pagination');

/**
 * Service xử lý nghiệp vụ Đánh giá Homestay (Reviews & Rating Calculation)
 */
class ReviewService {
  /**
   * Tạo đánh giá mới cho homestay sau khi kỳ nghỉ kết thúc
   */
  static async createReview(userId, { booking_id, rating, comment }) {
    // 1. Kiểm tra đơn đặt phòng có tồn tại không
    const booking = await BookingModel.findById(booking_id);
    if (!booking) {
      throw ApiError.notFound('Không tìm thấy đơn đặt phòng');
    }

    // 2. Kiểm tra quyền sở hữu đơn đặt phòng
    if (booking.user_id !== userId) {
      throw ApiError.forbidden('Bạn chỉ có thể đánh giá cho đơn đặt phòng của chính mình');
    }

    // 3. Quy tắc cốt lõi: Chỉ được review sau khi booking đã COMPLETED
    if (booking.status !== 'COMPLETED') {
      throw ApiError.badRequest(
        `Bạn chỉ có thể đánh giá sau khi hoàn thành kỳ nghỉ (Trạng thái hiện tại: "${booking.status}")`
      );
    }

    // 4. Quy tắc cốt lõi: Mỗi booking chỉ được review 1 lần duy nhất
    const existingReview = await ReviewModel.findByBookingId(booking_id);
    if (existingReview) {
      throw ApiError.conflict('Đơn đặt phòng này đã được đánh giá trước đó');
    }

    const conn = await pool.getConnection();
    try {
      await conn.beginTransaction();

      // 5. Thêm review mới vào cơ sở dữ liệu
      const reviewId = await ReviewModel.create(
        {
          bookingId: booking_id,
          userId,
          homestayId: booking.homestay_id,
          rating: Number(rating),
          comment
        },
        conn
      );

      // 6. Tính toán và cập nhật lại điểm trung bình avg_rating của Homestay
      await HomestayModel.updateAvgRating(booking.homestay_id, conn);

      await conn.commit();

      return await ReviewModel.findById(reviewId);
    } catch (error) {
      await conn.rollback();
      throw error;
    } finally {
      conn.release();
    }
  }

  /**
   * Lấy danh sách đánh giá của một Homestay
   */
  static async getHomestayReviews(homestayId, query) {
    const homestay = await HomestayModel.findById(homestayId);
    if (!homestay) {
      throw ApiError.notFound('Homestay không tồn tại');
    }

    const { page, limit, offset } = getPaginationParams(query, 10);
    const { items, total } = await ReviewModel.findByHomestayId(homestayId, limit, offset);
    const meta = buildPaginationMeta(total, page, limit);

    return {
      reviews: items,
      avg_rating: homestay.avg_rating,
      pagination: meta
    };
  }

  /**
   * Xóa đánh giá (Admin) và tính lại avg_rating
   */
  static async deleteReview(reviewId, currentUser) {
    const review = await ReviewModel.findById(reviewId);
    if (!review) {
      throw ApiError.notFound('Đánh giá không tồn tại');
    }

    if (currentUser.role !== 'admin') {
      throw ApiError.forbidden('Chỉ quản trị viên mới có quyền xóa đánh giá');
    }

    const conn = await pool.getConnection();
    try {
      await conn.beginTransaction();

      await ReviewModel.delete(reviewId, conn);
      // Cập nhật lại điểm sau khi xóa
      await HomestayModel.updateAvgRating(review.homestay_id, conn);

      await conn.commit();
      return true;
    } catch (error) {
      await conn.rollback();
      throw error;
    } finally {
      conn.release();
    }
  }
}

module.exports = ReviewService;
