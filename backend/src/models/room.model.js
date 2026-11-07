const { pool } = require('../config/database');

/**
 * Model quản lý dữ liệu bảng rooms (Raw SQL)
 */
class RoomModel {
  /**
   * Lấy danh sách phòng thuộc về một homestay
   */
  static async findByHomestayId(homestayId) {
    const sql = `
      SELECT id, homestay_id, name, price_per_night, capacity, bed_type, status, created_at, updated_at
      FROM rooms
      WHERE homestay_id = ?
      ORDER BY price_per_night ASC
    `;
    const [rows] = await pool.query(sql, [homestayId]);
    return rows;
  }

  /**
   * Tìm phòng theo ID kèm thông tin của homestay sở hữu
   */
  static async findById(id, conn = pool) {
    const sql = `
      SELECT 
        r.id,
        r.homestay_id,
        r.name,
        r.price_per_night,
        r.capacity,
        r.bed_type,
        r.status,
        r.created_at,
        r.updated_at,
        h.name as homestay_name,
        h.owner_id as homestay_owner_id,
        h.city as homestay_city,
        h.address as homestay_address
      FROM rooms r
      INNER JOIN homestays h ON r.homestay_id = h.id
      WHERE r.id = ?
      LIMIT 1
    `;
    const [rows] = await conn.query(sql, [id]);
    return rows[0] || null;
  }

  /**
   * Thêm phòng mới vào homestay (Admin)
   */
  static async create({ homestayId, name, pricePerNight, capacity, bedType, status = 'available' }) {
    const sql = `
      INSERT INTO rooms (homestay_id, name, price_per_night, capacity, bed_type, status)
      VALUES (?, ?, ?, ?, ?, ?)
    `;
    const [result] = await pool.query(sql, [
      homestayId,
      name,
      pricePerNight,
      capacity,
      bedType,
      status
    ]);
    return result.insertId;
  }

  /**
   * Cập nhật thông tin phòng
   */
  static async update(id, { name, pricePerNight, capacity, bedType, status }) {
    const fields = [];
    const params = [];

    if (name !== undefined) {
      fields.push('name = ?');
      params.push(name);
    }
    if (pricePerNight !== undefined) {
      fields.push('price_per_night = ?');
      params.push(pricePerNight);
    }
    if (capacity !== undefined) {
      fields.push('capacity = ?');
      params.push(capacity);
    }
    if (bedType !== undefined) {
      fields.push('bed_type = ?');
      params.push(bedType);
    }
    if (status !== undefined) {
      fields.push('status = ?');
      params.push(status);
    }

    if (fields.length === 0) return false;

    params.push(id);
    const sql = `UPDATE rooms SET ${fields.join(', ')} WHERE id = ?`;
    const [result] = await pool.query(sql, params);
    return result.affectedRows > 0;
  }

  /**
   * Xóa phòng
   */
  static async delete(id) {
    const [result] = await pool.query('DELETE FROM rooms WHERE id = ?', [id]);
    return result.affectedRows > 0;
  }
}

module.exports = RoomModel;

