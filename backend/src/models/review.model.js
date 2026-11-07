const { pool } = require('../config/database');

/**
 * Model quản lý dữ liệu bảng reviews (Raw SQL)
 */
class ReviewModel {
  /**
   * Tạo đánh giá mới
   */
  static async create({ bookingId, userId, homestayId, rating, comment = null }, conn = pool) {
    const sql = `
      INSERT INTO reviews (booking_id, user_id, homestay_id, rating, comment)
      VALUES (?, ?, ?, ?, ?)
    `;
    const [result] = await conn.query(sql, [bookingId, userId, homestayId, rating, comment]);
    return result.insertId;
  }

  /**
   * Tìm đánh giá theo booking_id
   */
  static async findByBookingId(bookingId, conn = pool) {
    const sql = `
      SELECT id, booking_id, user_id, homestay_id, rating, comment, created_at
      FROM reviews
      WHERE booking_id = ?
      LIMIT 1
    `;
    const [rows] = await conn.query(sql, [bookingId]);
    return rows[0] || null;
  }

  /**
   * Lấy danh sách đánh giá của một homestay kèm thông tin người đánh giá
   */
  static async findByHomestayId(homestayId, limit = 10, offset = 0) {
    const countSql = `SELECT COUNT(*) as total FROM reviews WHERE homestay_id = ?`;
    const [countRows] = await pool.query(countSql, [homestayId]);
    const total = countRows[0]?.total || 0;

    const dataSql = `
      SELECT 
        rv.id,
        rv.booking_id,
        rv.user_id,
        rv.homestay_id,
        rv.rating,
        rv.comment,
        rv.created_at,
        u.full_name as user_name,
        r.name as room_name
      FROM reviews rv
      INNER JOIN users u ON rv.user_id = u.id
      INNER JOIN bookings b ON rv.booking_id = b.id
      INNER JOIN rooms r ON b.room_id = r.id
      WHERE rv.homestay_id = ?
      ORDER BY rv.id DESC
      LIMIT ? OFFSET ?
    `;
    const [rows] = await pool.query(dataSql, [homestayId, Number(limit), Number(offset)]);

    return {
      items: rows,
      total
    };
  }

  /**
   * Tìm đánh giá theo ID
   */
  static async findById(id) {
    const sql = `
      SELECT id, booking_id, user_id, homestay_id, rating, comment, created_at
      FROM reviews
      WHERE id = ?
      LIMIT 1
    `;
    const [rows] = await pool.query(sql, [id]);
    return rows[0] || null;
  }

  /**
   * Xóa đánh giá (Admin)
   */
  static async delete(id, conn = pool) {
    const [result] = await conn.query('DELETE FROM reviews WHERE id = ?', [id]);
    return result.affectedRows > 0;
  }
}

module.exports = ReviewModel;
