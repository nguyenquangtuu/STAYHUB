const { pool } = require('../config/database');

/**
 * Model quản lý dữ liệu bảng homestays (Raw SQL)
 */
class HomestayModel {
  /**
   * Lấy danh sách homestay kèm bộ lọc đa tiêu chí (city, minPrice, maxPrice, guests, amenities)
   */
  static async findAll({ city, minPrice, maxPrice, guests, amenities, limit = 10, offset = 0 }) {
    const conditions = ['1=1'];
    const params = [];

    // 1. Lọc theo thành phố
    if (city) {
      conditions.push('LOWER(h.city) LIKE LOWER(?)');
      params.push(`%${city.trim()}%`);
    }

    // 2. Lọc theo giá (minPrice, maxPrice) và số khách (guests) qua quan hệ bảng rooms
    const roomConditions = ["r.status = 'available'"];
    const roomParams = [];

    if (minPrice !== undefined && minPrice !== null && minPrice !== '') {
      roomConditions.push('r.price_per_night >= ?');
      roomParams.push(Number(minPrice));
    }
    if (maxPrice !== undefined && maxPrice !== null && maxPrice !== '') {
      roomConditions.push('r.price_per_night <= ?');
      roomParams.push(Number(maxPrice));
    }
    if (guests !== undefined && guests !== null && guests !== '') {
      roomConditions.push('r.capacity >= ?');
      roomParams.push(Number(guests));
    }

    // Nếu có tiêu chí lọc phòng, dùng EXISTS
    if (roomConditions.length > 1) {
      conditions.push(`EXISTS (
        SELECT 1 FROM rooms r 
        WHERE r.homestay_id = h.id AND ${roomConditions.join(' AND ')}
      )`);
      params.push(...roomParams);
    }

    // 3. Lọc theo tiện ích (amenities có thể là chuỗi phân tách bằng dấu phẩy: "wifi,pool")
    if (amenities) {
      const amenityList = Array.isArray(amenities)
        ? amenities
        : String(amenities).split(',').map((item) => item.trim()).filter(Boolean);

      for (const item of amenityList) {
        // Dùng JSON_CONTAINS kiểm tra phần tử trong mảng JSON
        conditions.push('JSON_CONTAINS(h.amenities, JSON_QUOTE(?))');
        params.push(item);
      }
    }

    const whereClause = conditions.join(' AND ');

    // Truy vấn tổng số lượng để phân trang
    const countSql = `SELECT COUNT(DISTINCT h.id) as total FROM homestays h WHERE ${whereClause}`;
    const [countRows] = await pool.query(countSql, params);
    const total = countRows[0]?.total || 0;

    // Truy vấn dữ liệu thực tế kèm thông tin chủ nhà và giá phòng thấp nhất
    const dataSql = `
      SELECT 
        h.id,
        h.owner_id,
        h.name,
        h.description,
        h.city,
        h.address,
        h.amenities,
        h.avg_rating,
        h.created_at,
        h.updated_at,
        u.full_name as owner_name,
        u.email as owner_email,
        u.phone as owner_phone,
        MIN(r.price_per_night) as min_room_price,
        COUNT(r.id) as total_rooms
      FROM homestays h
      INNER JOIN users u ON h.owner_id = u.id
      LEFT JOIN rooms r ON h.id = r.homestay_id AND r.status = 'available'
      WHERE ${whereClause}
      GROUP BY h.id, u.id
      ORDER BY h.id DESC
      LIMIT ? OFFSET ?
    `;

    const [rows] = await pool.query(dataSql, [...params, Number(limit), Number(offset)]);

    // Parse JSON field amenities nếu database trả về chuỗi string
    const formattedRows = rows.map((row) => ({
      ...row,
      amenities: typeof row.amenities === 'string' ? JSON.parse(row.amenities) : (row.amenities || [])
    }));

    return {
      items: formattedRows,
      total
    };
  }

  /**
   * Chi tiết homestay theo ID kèm danh sách phòng
   */
  static async findById(id) {
    const sql = `
      SELECT 
        h.id,
        h.owner_id,
        h.name,
        h.description,
        h.city,
        h.address,
        h.amenities,
        h.avg_rating,
        h.created_at,
        h.updated_at,
        u.full_name as owner_name,
        u.email as owner_email,
        u.phone as owner_phone
      FROM homestays h
      INNER JOIN users u ON h.owner_id = u.id
      WHERE h.id = ?
      LIMIT 1
    `;
    const [rows] = await pool.query(sql, [id]);
    if (rows.length === 0) return null;

    const homestay = rows[0];
    homestay.amenities = typeof homestay.amenities === 'string'
      ? JSON.parse(homestay.amenities)
      : (homestay.amenities || []);

    return homestay;
  }

  /**
   * Tạo mới homestay (Admin)
   */
  static async create({ ownerId, name, description, city, address, amenities = [] }) {
    const amenitiesJson = JSON.stringify(amenities);
    const sql = `
      INSERT INTO homestays (owner_id, name, description, city, address, amenities, avg_rating)
      VALUES (?, ?, ?, ?, ?, ?, 0.0)
    `;
    const [result] = await pool.query(sql, [ownerId, name, description, city, address, amenitiesJson]);
    return result.insertId;
  }

  /**
   * Cập nhật homestay
   */
  static async update(id, { name, description, city, address, amenities }) {
    const fields = [];
    const params = [];

    if (name !== undefined) {
      fields.push('name = ?');
      params.push(name);
    }
    if (description !== undefined) {
      fields.push('description = ?');
      params.push(description);
    }
    if (city !== undefined) {
      fields.push('city = ?');
      params.push(city);
    }
    if (address !== undefined) {
      fields.push('address = ?');
      params.push(address);
    }
    if (amenities !== undefined) {
      fields.push('amenities = ?');
      params.push(JSON.stringify(amenities));
    }

    if (fields.length === 0) return false;

    params.push(id);
    const sql = `UPDATE homestays SET ${fields.join(', ')} WHERE id = ?`;
    const [result] = await pool.query(sql, params);
    return result.affectedRows > 0;
  }

  /**
   * Xóa homestay
   */
  static async delete(id) {
    const [result] = await pool.query('DELETE FROM homestays WHERE id = ?', [id]);
    return result.affectedRows > 0;
  }

  /**
   * Tính toán và cập nhật lại điểm đánh giá trung bình của homestay
   * @param {number} homestayId
   * @param {import('mysql2/promise').Connection|null} conn
   */
  static async updateAvgRating(homestayId, conn = pool) {
    const sql = `
      UPDATE homestays 
      SET avg_rating = COALESCE(
        (SELECT ROUND(AVG(rating), 1) FROM reviews WHERE homestay_id = ?),
        0.0
      )
      WHERE id = ?
    `;
    await conn.query(sql, [homestayId, homestayId]);
  }
}

module.exports = HomestayModel;
