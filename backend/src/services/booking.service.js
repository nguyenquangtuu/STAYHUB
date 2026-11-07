const { pool } = require('../config/database');
const BookingModel = require('../models/booking.model');
const RoomModel = require('../models/room.model');
const ApiError = require('../utils/ApiError');
const { calculateNights, isCheckInFromToday, canCancelBooking } = require('../utils/dateHelper');
const { getPaginationParams, buildPaginationMeta } = require('../utils/pagination');

/**
 * Service xử lý nghiệp vụ Đặt phòng Homestay (Booking)
 * Sử dụng ACID Transactions và SELECT ... FOR UPDATE chống Race Condition
 */
class BookingService {
  /**
   * Tạo đơn đặt phòng mới (User)
   * Sử dụng MySQL Transaction và Row-level lock để ngăn ngừa đặt trùng lặp cùng lúc
   */
  static async createBooking(userId, { room_id, check_in, check_out, guests, note }) {
    // 1. Kiểm tra tính hợp lệ cơ bản của ngày tháng
    if (!isCheckInFromToday(check_in)) {
      throw ApiError.badRequest('Ngày nhận phòng (check_in) không được nhỏ hơn ngày hôm nay');
    }

    const nights = calculateNights(check_in, check_out);
    if (nights <= 0) {
      throw ApiError.badRequest('Ngày trả phòng (check_out) phải lớn hơn ngày nhận phòng (check_in) ít nhất 1 đêm');
    }

    const conn = await pool.getConnection();
    try {
      // 2. Bắt đầu Transaction
      await conn.beginTransaction();

      // 3. Khóa dòng bản ghi phòng để kiểm tra trạng thái và sức chứa (SELECT ... FOR UPDATE)
      const [roomRows] = await conn.query(
        'SELECT id, homestay_id, name, price_per_night, capacity, status FROM rooms WHERE id = ? FOR UPDATE',
        [room_id]
      );

      if (roomRows.length === 0) {
        throw ApiError.notFound('Phòng yêu cầu không tồn tại');
      }

      const room = roomRows[0];

      if (room.status !== 'available') {
        throw ApiError.badRequest(`Phòng "${room.name}" hiện không sẵn sàng (đang bảo trì)`);
      }

      if (Number(guests) > Number(room.capacity)) {
        throw ApiError.badRequest(
          `Số lượng khách (${guests} người) vượt quá sức chứa tối đa của phòng (${room.capacity} người)`
        );
      }

      // 4. Kiểm tra phòng có bị trùng lịch hay không (có lock FOR UPDATE)
      const isOverlapped = await BookingModel.checkOverlap(
        room_id,
        check_in,
        check_out,
        null,
        conn,
        true // lock FOR UPDATE
      );

      if (isOverlapped) {
        throw ApiError.conflict('Phòng đã được đặt hoặc đang có đơn chờ duyệt trong thời gian này. Vui lòng chọn ngày khác');
      }

      // 5. Tính toán tổng tiền: số đêm * giá mỗi đêm
      const totalPrice = nights * Number(room.price_per_night);

      // 6. Tạo booking với trạng thái PENDING
      const bookingId = await BookingModel.create(
        {
          userId,
          roomId: room_id,
          checkIn: check_in,
          checkOut: check_out,
          guests: Number(guests),
          totalPrice,
          status: 'PENDING',
          note
        },
        conn
      );

      // 7. Commit Transaction
      await conn.commit();

      // 8. Trả về thông tin chi tiết đơn đặt phòng vừa tạo
      return await BookingModel.findById(bookingId);
    } catch (error) {
      // Rollback Transaction nếu có bất kỳ lỗi nào xảy ra
      await conn.rollback();
      throw error;
    } finally {
      conn.release();
    }
  }

  /**
   * Lấy danh sách booking của người dùng hiện tại
   */
  static async getMyBookings(userId, query) {
    const { page, limit, offset } = getPaginationParams(query, 10);
    const { items, total } = await BookingModel.findByUserId(userId, limit, offset);
    const meta = buildPaginationMeta(total, page, limit);

    return {
      bookings: items,
      pagination: meta
    };
  }

  /**
   * Xem chi tiết một booking
   */
  static async getBookingById(bookingId, currentUser) {
    const booking = await BookingModel.findById(bookingId);
    if (!booking) {
      throw ApiError.notFound('Không tìm thấy đơn đặt phòng');
    }

    // Chỉ chủ booking hoặc Admin hoặc chủ Homestay mới có quyền xem
    const isOwner = booking.user_id === currentUser.id;
    const isHomestayOwner = booking.homestay_owner_id === currentUser.id;
    const isAdmin = currentUser.role === 'admin';

    if (!isOwner && !isHomestayOwner && !isAdmin) {
      throw ApiError.forbidden('Bạn không có quyền truy cập thông tin đơn đặt phòng này');
    }

    return booking;
  }

  /**
   * Hủy đơn đặt phòng (Chỉ cho chủ booking, status PENDING/CONFIRMED và trước check_in >= 24h)
   */
  static async cancelBooking(bookingId, currentUser) {
    const booking = await BookingModel.findById(bookingId);
    if (!booking) {
      throw ApiError.notFound('Không tìm thấy đơn đặt phòng');
    }

    // 1. Kiểm tra quyền hủy: Phải là chủ booking
    if (booking.user_id !== currentUser.id && currentUser.role !== 'admin') {
      throw ApiError.forbidden('Bạn chỉ có thể hủy đơn đặt phòng của chính mình');
    }

    // 2. Kiểm tra trạng thái: Chỉ được hủy khi status = PENDING hoặc CONFIRMED
    if (!['PENDING', 'CONFIRMED'].includes(booking.status)) {
      throw ApiError.badRequest(`Không thể hủy đơn đặt phòng đang ở trạng thái "${booking.status}"`);
    }

    // 3. Kiểm tra thời gian: Phải trước giờ check_in ít nhất 24 giờ
    if (!canCancelBooking(booking.check_in)) {
      throw ApiError.badRequest(
        'Đã quá thời hạn hủy phòng (Chính sách chỉ cho phép hủy trước ngày nhận phòng ít nhất 24 giờ)'
      );
    }

    // 4. Cập nhật trạng thái thành CANCELLED
    await BookingModel.updateStatus(bookingId, 'CANCELLED');
    return await BookingModel.findById(bookingId);
  }

  /**
   * Cập nhật trạng thái đơn đặt phòng (Dành cho Admin - Confirm/Reject/Complete)
   */
  static async updateBookingStatus(bookingId, currentUser, newStatus) {
    const booking = await BookingModel.findById(bookingId);
    if (!booking) {
      throw ApiError.notFound('Không tìm thấy đơn đặt phòng');
    }

    // Quyền: Admin hoặc chủ sở hữu homestay
    const isHomestayOwner = booking.homestay_owner_id === currentUser.id;
    const isAdmin = currentUser.role === 'admin';

    if (!isHomestayOwner && !isAdmin) {
      throw ApiError.forbidden('Bạn không có quyền cập nhật trạng thái đơn đặt phòng này');
    }

    // Kiểm tra quy tắc chuyển trạng thái
    if (booking.status === 'CANCELLED') {
      throw ApiError.badRequest('Đơn đặt phòng đã bị hủy, không thể thay đổi trạng thái');
    }
    if (booking.status === 'COMPLETED') {
      throw ApiError.badRequest('Đơn đặt phòng đã hoàn tất, không thể thay đổi trạng thái');
    }

    await BookingModel.updateStatus(bookingId, newStatus);
    return await BookingModel.findById(bookingId);
  }

  /**
   * Quản lý tất cả booking (Admin)
   */
  static async getAllBookings(query) {
    const { page, limit, offset } = getPaginationParams(query, 10);
    const { status, homestayId } = query;

    const { items, total } = await BookingModel.findAll({
      status,
      homestayId,
      limit,
      offset
    });

    const meta = buildPaginationMeta(total, page, limit);
    return {
      bookings: items,
      pagination: meta
    };
  }
}

module.exports = BookingService;

