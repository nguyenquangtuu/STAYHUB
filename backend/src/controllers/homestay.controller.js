const HomestayService = require('../services/homestay.service');
const { sendSuccess } = require('../utils/response');

/**
 * Controller xử lý các yêu cầu liên quan đến Homestay
 */
class HomestayController {
  /**
   * Lấy danh sách homestay kèm bộ lọc & phân trang
   */
  static async getAll(req, res, next) {
    try {
      const result = await HomestayService.getAllHomestays(req.query);
      return sendSuccess(res, result, 'Lấy danh sách homestay thành công', 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Chi tiết homestay theo ID
   */
  static async getById(req, res, next) {
    try {
      const homestay = await HomestayService.getHomestayById(req.params.id);
      return sendSuccess(res, homestay, 'Lấy thông tin chi tiết homestay thành công', 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Tạo mới homestay (Admin)
   */
  static async create(req, res, next) {
    try {
      const homestay = await HomestayService.createHomestay(req.user.id, req.body);
      return sendSuccess(res, homestay, 'Tạo homestay mới thành công', 201);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Cập nhật homestay (Admin - Chủ sở hữu)
   */
  static async update(req, res, next) {
    try {
      const updated = await HomestayService.updateHomestay(req.params.id, req.user, req.body);
      return sendSuccess(res, updated, 'Cập nhật homestay thành công', 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Xóa homestay (Admin)
   */
  static async delete(req, res, next) {
    try {
      await HomestayService.deleteHomestay(req.params.id, req.user);
      return sendSuccess(res, null, 'Xóa homestay thành công', 200);
    } catch (error) {
      next(error);
    }
  }
}

module.exports = HomestayController;

