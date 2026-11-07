const { pool } = require('../config/database');

/**
 * Model quản lý dữ liệu bảng bookings (Raw SQL)
 */
class BookingModel {
  /**
   * Kiểm tra trùng lịch booking
   * Quy tắc: Phòng bị trùng nếu có booking status IN ('PENDING', 'CONFIRMED') thỏa mãn:
   * check_in < newCheckOut AND check_out > newCheckIn
   * @param {number} roomId
   * @param {string} checkIn Ngày check-in mới (YYYY-MM-DD)
   * @param {string} checkOut Ngày check-out mới (YYYY-MM-DD)
   * @param {number|null} excludeBookingId Bỏ qua booking ID này (dùng khi cập nhật)
   * @param {import('mysql2/promise').Connection} conn Connection trong transaction hoặc pool
   * @param {boolean} forUpdate Khóa dòng FOR UPDATE để chống race condition
   * @returns {Promise<boolean>} true nếu bị trùng lịch, false nếu phòng còn trống
   */
  static async checkOverlap(roomId, checkIn, checkOut, excludeBookingId = null, conn = pool, forUpdate = false) {
    let sql = `
      SELECT 1 FROM bookings 
      WHERE room_id = ? 
        AND status IN ('PENDING', 'CONFIRMED')
        AND check_in < ? AND check_out > ?
    `;
    const params = [roomId, checkOut, checkIn];

    if (excludeBookingId) {
      sql += ' AND id != ?';
      params.push(excludeBookingId);
    }

    sql += ' LIMIT 1';

    if (forUpdate) {
      sql += ' FOR UPDATE';
    }

    const [rows] = await conn.query(sql, params);
    return rows.length > 0;
  }

  /**
   * Tạo đơn đặt phòng mới
   */
  static async create({ userId, roomId, checkIn, checkOut, guests, totalPrice, status = 'PENDING', note = null }, conn = pool) {
    const sql = `
      INSERT INTO bookings (user_id, room_id, check_in, check_out, guests, total_price, status, note)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    `;
    const [result] = await conn.query(sql, [
      userId,
      roomId,
      checkIn,
      checkOut,
      guests,
      totalPrice,
      status,
      note
    ]);
    return result.insertId;
  }

  /**
   * Lấy chi tiết đơn đặt phòng theo ID kèm thông tin Room, Homestay, User
   */
  static async findById(id, conn = pool) {
    const sql = `
      SELECT 
        b.id,
        b.user_id,
        b.room_id,
        b.check_in,
        b.check_out,
        b.guests,
        b.total_price,
        b.status,
        b.note,
        b.created_at,
        b.updated_at,
        u.full_name as user_name,
        u.email as user_email,
        u.phone as user_phone,
        r.name as room_name,
        r.price_per_night,
        r.bed_type,
        h.id as homestay_id,
        h.name as homestay_name,
        h.city as homestay_city,
        h.address as homestay_address,
        h.owner_id as homestay_owner_id
      FROM bookings b
      INNER JOIN users u ON b.user_id = u.id
      INNER JOIN rooms r ON b.room_id = r.id
      INNER JOIN homestays h ON r.homestay_id = h.id
      WHERE b.id = ?
      LIMIT 1
    `;
    const [rows] = await conn.query(sql, [id]);
    return rows[0] || null;
  }

  /**
   * Lấy danh sách booking của một User (Lịch sử đặt phòng)
   */
  static async findByUserId(userId, limit = 10, offset = 0) {
    const countSql = `SELECT COUNT(*) as total FROM bookings WHERE user_id = ?`;
    const [countRows] = await pool.query(countSql, [userId]);
    const total = countRows[0]?.total || 0;

    const dataSql = `
      SELECT 
        b.id,
        b.user_id,
        b.room_id,
        b.check_in,
        b.check_out,
        b.guests,
        b.total_price,
        b.status,
        b.note,
        b.created_at,
        b.updated_at,
        r.name as room_name,
        r.price_per_night,
        h.id as homestay_id,
        h.name as homestay_name,
        h.city as homestay_city,
        h.address as homestay_address,
        rv.id as review_id,
        rv.rating as review_rating
      FROM bookings b
      INNER JOIN rooms r ON b.room_id = r.id
      INNER JOIN homestays h ON r.homestay_id = h.id
      LEFT JOIN reviews rv ON b.id = rv.booking_id
      WHERE b.user_id = ?
      ORDER BY b.id DESC
      LIMIT ? OFFSET ?
    `;
    const [rows] = await pool.query(dataSql, [userId, Number(limit), Number(offset)]);

    return {
      items: rows,
      total
    };
  }

  /**
   * Quản lý tất cả booking trong hệ thống (Dành cho Admin)
   */
  static async findAll({ status, homestayId, limit = 10, offset = 0 }) {
    const conditions = ['1=1'];
    const params = [];

    if (status) {
      conditions.push('b.status = ?');
      params.push(status);
    }
    if (homestayId) {
      conditions.push('h.id = ?');
      params.push(Number(homestayId));
    }

    const whereClause = conditions.join(' AND ');

    const countSql = `
      SELECT COUNT(*) as total 
      FROM bookings b
      INNER JOIN rooms r ON b.room_id = r.id
      INNER JOIN homestays h ON r.homestay_id = h.id
      WHERE ${whereClause}
    `;
    const [countRows] = await pool.query(countSql, params);
    const total = countRows[0]?.total || 0;

    const dataSql = `
      SELECT 
        b.id,
        b.user_id,
        b.room_id,
        b.check_in,
        b.check_out,
        b.guests,
        b.total_price,
        b.status,
        b.note,
        b.created_at,
        b.updated_at,
        u.full_name as user_name,
        u.email as user_email,
        u.phone as user_phone,
        r.name as room_name,
        h.id as homestay_id,
        h.name as homestay_name,
        h.city as homestay_city
      FROM bookings b
      INNER JOIN users u ON b.user_id = u.id
      INNER JOIN rooms r ON b.room_id = r.id
      INNER JOIN homestays h ON r.homestay_id = h.id
      WHERE ${whereClause}
      ORDER BY b.id DESC
      LIMIT ? OFFSET ?
    `;
    const [rows] = await pool.query(dataSql, [...params, Number(limit), Number(offset)]);

    return {
      items: rows,
      total
    };
  }

  /**
   * Cập nhật trạng thái đơn đặt phòng
   */
  static async updateStatus(id, status, conn = pool) {
    const sql = `UPDATE bookings SET status = ? WHERE id = ?`;
    const [result] = await conn.query(sql, [status, id]);
    return result.affectedRows > 0;
  }
}

module.exports = BookingModel;

