const { pool } = require('../config/database');

/**
 * Model quản lý dữ liệu bảng users (Raw SQL)
 */
class UserModel {
  /**
   * Tìm người dùng theo Email
   * @param {string} email
   * @returns {Promise<object|null>}
   */
  static async findByEmail(email) {
    const [rows] = await pool.query(
      `SELECT id, full_name, email, password_hash, phone, role, created_at, updated_at 
       FROM users 
       WHERE email = ? 
       LIMIT 1`,
      [email]
    );
    return rows[0] || null;
  }

  /**
   * Tìm người dùng theo ID (không trả về password_hash)
   * @param {number} id
   * @returns {Promise<object|null>}
   */
  static async findById(id) {
    const [rows] = await pool.query(
      `SELECT id, full_name, email, phone, role, created_at, updated_at 
       FROM users 
       WHERE id = ? 
       LIMIT 1`,
      [id]
    );
    return rows[0] || null;
  }

  /**
   * Tạo người dùng mới
   * @param {{ fullName: string, email: string, passwordHash: string, phone: string|null, role: string }} data
   * @returns {Promise<number>} ID của user vừa tạo
   */
  static async create({ fullName, email, passwordHash, phone = null, role = 'user' }) {
    const [result] = await pool.query(
      `INSERT INTO users (full_name, email, password_hash, phone, role) 
       VALUES (?, ?, ?, ?, ?)`,
      [fullName, email, passwordHash, phone, role]
    );
    return result.insertId;
  }

  /**
   * Cập nhật thông tin người dùng (Họ tên, Số điện thoại)
   * @param {number} id
   * @param {{ fullName?: string, phone?: string }} data
   * @returns {Promise<boolean>}
   */
  static async update(id, { fullName, phone }) {
    const fields = [];
    const values = [];

    if (fullName !== undefined) {
      fields.push('full_name = ?');
      values.push(fullName);
    }
    if (phone !== undefined) {
      fields.push('phone = ?');
      values.push(phone);
    }

    if (fields.length === 0) return false;

    values.push(id);
    const [result] = await pool.query(
      `UPDATE users SET ${fields.join(', ')} WHERE id = ?`,
      values
    );
    return result.affectedRows > 0;
  }
}

module.exports = UserModel;

