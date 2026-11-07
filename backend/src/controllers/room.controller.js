const RoomService = require('../services/room.service');
const { sendSuccess } = require('../utils/response');

/**
 * Controller xử lý các yêu cầu liên quan đến Room (Phòng nghỉ)
 */
class RoomController {
  /**
   * Lấy danh sách phòng thuộc homestay
   */
  static async getByHomestay(req, res, next) {
    try {
      const rooms = await RoomService.getRoomsByHomestayId(req.params.id);
      return sendSuccess(res, rooms, 'Lấy danh sách phòng thành công', 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Lấy chi tiết phòng theo ID
   */
  static async getById(req, res, next) {
    try {
      const room = await RoomService.getRoomById(req.params.id);
      return sendSuccess(res, room, 'Lấy thông tin chi tiết phòng thành công', 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Kiểm tra tình trạng phòng trống (Availability)
   */
  static async checkAvailability(req, res, next) {
    try {
      const { checkIn, checkOut } = req.query;
      const result = await RoomService.checkAvailability(req.params.id, checkIn, checkOut);
      return sendSuccess(res, result, 'Kiểm tra tình trạng phòng thành công', 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Tạo phòng mới cho homestay (Admin)
   */
  static async create(req, res, next) {
    try {
      const room = await RoomService.createRoom(req.params.id, req.user, req.body);
      return sendSuccess(res, room, 'Thêm phòng mới thành công', 201);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Cập nhật thông tin phòng (Admin)
   */
  static async update(req, res, next) {
    try {
      const room = await RoomService.updateRoom(req.params.id, req.user, req.body);
      return sendSuccess(res, room, 'Cập nhật thông tin phòng thành công', 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Xóa phòng (Admin)
   */
  static async delete(req, res, next) {
    try {
      await RoomService.deleteRoom(req.params.id, req.user);
      return sendSuccess(res, null, 'Xóa phòng thành công', 200);
    } catch (error) {
      next(error);
    }
  }
}

module.exports = RoomController;
