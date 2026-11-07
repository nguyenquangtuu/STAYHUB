const HomestayModel = require('../models/homestay.model');
const RoomModel = require('../models/room.model');
const ApiError = require('../utils/ApiError');
const { getPaginationParams, buildPaginationMeta } = require('../utils/pagination');

/**
 * Service xử lý nghiệp vụ Homestay (CRUD, Lọc tìm kiếm, Kiểm tra quyền sở hữu)
 */
class HomestayService {
  /**
   * Lấy danh sách homestay với bộ lọc và phân trang
   */
  static async getAllHomestays(query) {
    const { page, limit, offset } = getPaginationParams(query, 10);
    const { city, minPrice, maxPrice, guests, amenities } = query;

    const { items, total } = await HomestayModel.findAll({
      city,
      minPrice,
      maxPrice,
      guests,
      amenities,
      limit,
      offset
    });

    const meta = buildPaginationMeta(total, page, limit);
    return {
      homestays: items,
      pagination: meta
    };
  }

  /**
   * Chi tiết homestay kèm danh sách phòng
   */
  static async getHomestayById(id) {
    const homestay = await HomestayModel.findById(id);
    if (!homestay) {
      throw ApiError.notFound('Không tìm thấy homestay với ID đã cung cấp');
    }

    // Lấy kèm danh sách phòng của homestay này
    const rooms = await RoomModel.findByHomestayId(id);
    return {
      ...homestay,
      rooms
    };
  }

  /**
   * Tạo homestay mới (Chỉ dành cho Admin/Chủ homestay)
   */
  static async createHomestay(ownerId, { name, description, city, address, amenities = [] }) {
    const newId = await HomestayModel.create({
      ownerId,
      name,
      description,
      city,
      address,
      amenities
    });

    return await HomestayModel.findById(newId);
  }

  /**
   * Cập nhật homestay (Kiểm tra quyền sở hữu)
   */
  static async updateHomestay(id, currentUser, updateData) {
    const homestay = await HomestayModel.findById(id);
    if (!homestay) {
      throw ApiError.notFound('Homestay không tồn tại');
    }

    // Kiểm tra quyền: Người tạo (owner) hoặc Admin hệ thống
    if (homestay.owner_id !== currentUser.id && currentUser.role !== 'admin') {
      throw ApiError.forbidden('Bạn không phải chủ sở hữu của homestay này');
    }

    await HomestayModel.update(id, updateData);
    return await HomestayModel.findById(id);
  }

  /**
   * Xóa homestay
   */
  static async deleteHomestay(id, currentUser) {
    const homestay = await HomestayModel.findById(id);
    if (!homestay) {
      throw ApiError.notFound('Homestay không tồn tại');
    }

    // Kiểm tra quyền
    if (homestay.owner_id !== currentUser.id && currentUser.role !== 'admin') {
      throw ApiError.forbidden('Bạn không có quyền xóa homestay này');
    }

    await HomestayModel.delete(id);
    return true;
  }
}

module.exports = HomestayService;
