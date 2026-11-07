const RoomModel = require('../models/room.model');
const HomestayModel = require('../models/homestay.model');
const BookingModel = require('../models/booking.model');
const ApiError = require('../utils/ApiError');
const { calculateNights, isCheckInFromToday } = require('../utils/dateHelper');

/**
 * Service xử lý nghiệp vụ Quản lý Phòng & Kiểm tra lịch trống
 */
class RoomService {
  /**
   * Lấy danh sách phòng của một homestay
   */
  static async getRoomsByHomestayId(homestayId) {
    const homestay = await HomestayModel.findById(homestayId);
    if (!homestay) {
      throw ApiError.notFound('Homestay không tồn tại');
    }
    return await RoomModel.findByHomestayId(homestayId);
  }

  /**
   * Lấy chi tiết phòng
   */
  static async getRoomById(id) {
    const room = await RoomModel.findById(id);
    if (!room) {
      throw ApiError.notFound('Không tìm thấy phòng với ID đã cung cấp');
    }
    return room;
  }

  /**
   * Kiểm tra tình trạng phòng trống trong khoảng thời gian check_in -> check_out
   */
  static async checkAvailability(roomId, checkIn, checkOut) {
    // 1. Kiểm tra phòng có tồn tại không
    const room = await RoomModel.findById(roomId);
    if (!room) {
      throw ApiError.notFound('Không tìm thấy phòng');
    }

    // 2. Validate ngày
    if (!checkIn || !checkOut) {
      throw ApiError.badRequest('Vui lòng cung cấp đầy đủ checkIn và checkOut (định dạng YYYY-MM-DD)');
    }

    if (!isCheckInFromToday(checkIn)) {
      throw ApiError.badRequest('Ngày check_in không được nhỏ hơn ngày hôm nay');
    }

    const nights = calculateNights(checkIn, checkOut);
    if (nights <= 0) {
      throw ApiError.badRequest('Ngày check_out phải lớn hơn ngày check_in ít nhất 1 đêm');
    }

    // Nếu phòng đang bảo trì
    if (room.status !== 'available') {
      return {
        available: false,
        reason: 'Phòng hiện đang trong trạng thái bảo trì',
        room
      };
    }

    // 3. Kiểm tra xem có booking trùng lịch không
    const isOverlapped = await BookingModel.checkOverlap(roomId, checkIn, checkOut);
    const totalPrice = nights * Number(room.price_per_night);

    return {
      available: !isOverlapped,
      reason: isOverlapped ? 'Phòng đã có khách đặt trong khoảng thời gian này' : 'Phòng trống, có thể đặt',
      room: {
        id: room.id,
        name: room.name,
        price_per_night: room.price_per_night,
        capacity: room.capacity,
        bed_type: room.bed_type
      },
      check_in: checkIn,
      check_out: checkOut,
      nights,
      total_price: totalPrice
    };
  }

  /**
   * Thêm phòng mới vào homestay (Admin)
   */
  static async createRoom(homestayId, currentUser, { name, price_per_night, capacity, bed_type, status = 'available' }) {
    const homestay = await HomestayModel.findById(homestayId);
    if (!homestay) {
      throw ApiError.notFound('Homestay không tồn tại');
    }

    if (homestay.owner_id !== currentUser.id && currentUser.role !== 'admin') {
      throw ApiError.forbidden('Bạn không có quyền thêm phòng vào homestay này');
    }

    const roomId = await RoomModel.create({
      homestayId,
      name,
      pricePerNight: price_per_night,
      capacity,
      bedType: bed_type,
      status
    });

    return await RoomModel.findById(roomId);
  }

  /**
   * Cập nhật thông tin phòng (Admin)
   */
  static async updateRoom(id, currentUser, updateData) {
    const room = await RoomModel.findById(id);
    if (!room) {
      throw ApiError.notFound('Phòng không tồn tại');
    }

    if (room.homestay_owner_id !== currentUser.id && currentUser.role !== 'admin') {
      throw ApiError.forbidden('Bạn không có quyền sửa thông tin phòng này');
    }

    await RoomModel.update(id, {
      name: updateData.name,
      pricePerNight: updateData.price_per_night,
      capacity: updateData.capacity,
      bedType: updateData.bed_type,
      status: updateData.status
    });

    return await RoomModel.findById(id);
  }

  /**
   * Xóa phòng (Admin)
   */
  static async deleteRoom(id, currentUser) {
    const room = await RoomModel.findById(id);
    if (!room) {
      throw ApiError.notFound('Phòng không tồn tại');
    }

    if (room.homestay_owner_id !== currentUser.id && currentUser.role !== 'admin') {
      throw ApiError.forbidden('Bạn không có quyền xóa phòng này');
    }

    await RoomModel.delete(id);
    return true;
  }
}

module.exports = RoomService;
