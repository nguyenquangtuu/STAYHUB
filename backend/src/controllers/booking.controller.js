const BookingService = require('../services/booking.service');
const { sendSuccess } = require('../utils/response');

/**
 * Controller xử lý các yêu cầu liên quan đến Đặt phòng (Bookings)
 */
class BookingController {
  /**
   * Tạo đơn đặt phòng mới (User)
   */
  static async create(req, res, next) {
    try {
      const { guests, note } = req.body;
      const room_id = req.body.room_id || req.body.roomId;
      const check_in = req.body.check_in || req.body.checkIn;
      const check_out = req.body.check_out || req.body.checkOut;

      const booking = await BookingService.createBooking(req.user.id, {
        room_id,
        check_in,
        check_out,
        guests,
        note
      });
      return sendSuccess(res, booking, 'Đặt phòng thành công! Đơn của bạn đang chờ xác nhận', 201);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Lấy lịch sử đặt phòng của người dùng hiện tại
   */
  static async getMyBookings(req, res, next) {
    try {
      const result = await BookingService.getMyBookings(req.user.id, req.query);
      return sendSuccess(res, result, 'Lấy danh sách đơn đặt phòng thành công', 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Xem chi tiết đơn đặt phòng
   */
  static async getById(req, res, next) {
    try {
      const booking = await BookingService.getBookingById(req.params.id, req.user);
      return sendSuccess(res, booking, 'Lấy thông tin đơn đặt phòng thành công', 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Hủy đơn đặt phòng (User / Admin)
   */
  static async cancel(req, res, next) {
    try {
      const booking = await BookingService.cancelBooking(req.params.id, req.user);
      return sendSuccess(res, booking, 'Hủy đơn đặt phòng thành công', 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Cập nhật trạng thái đơn đặt phòng (Admin - confirm/reject/complete)
   */
  static async updateStatus(req, res, next) {
    try {
      const { status } = req.body;
      const booking = await BookingService.updateBookingStatus(req.params.id, req.user, status);
      return sendSuccess(res, booking, `Cập nhật trạng thái đơn đặt phòng thành "${status}" thành công`, 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Lấy toàn bộ đơn đặt phòng trong hệ thống (Admin)
   */
  static async getAll(req, res, next) {
    try {
      const result = await BookingService.getAllBookings(req.query);
      return sendSuccess(res, result, 'Lấy danh sách tất cả đơn đặt phòng thành công', 200);
    } catch (error) {
      next(error);
    }
  }
}

module.exports = BookingController;
