const ReviewService = require('../services/review.service');
const { sendSuccess } = require('../utils/response');

/**
 * Controller xử lý các yêu cầu liên quan đến Đánh giá (Reviews)
 */
class ReviewController {
  /**
   * Tạo đánh giá mới cho booking đã hoàn thành
   */
  static async create(req, res, next) {
    try {
      const { rating, comment } = req.body;
      const booking_id = req.body.booking_id || req.body.bookingId;
      const review = await ReviewService.createReview(req.user.id, {
        booking_id,
        rating,
        comment
      });
      return sendSuccess(res, review, 'Cảm ơn bạn đã gửi đánh giá cho kỳ nghỉ!', 201);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Lấy danh sách đánh giá của homestay kèm điểm trung bình
   */
  static async getByHomestay(req, res, next) {
    try {
      const result = await ReviewService.getHomestayReviews(req.params.id, req.query);
      return sendSuccess(res, result, 'Lấy danh sách đánh giá thành công', 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Xóa đánh giá (Admin)
   */
  static async delete(req, res, next) {
    try {
      await ReviewService.deleteReview(req.params.id, req.user);
      return sendSuccess(res, null, 'Xóa đánh giá thành công', 200);
    } catch (error) {
      next(error);
    }
  }
}

module.exports = ReviewController;
