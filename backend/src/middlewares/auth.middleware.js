const jwt = require('jsonwebtoken');
const env = require('../config/env');
const { pool } = require('../config/database');
const ApiError = require('../utils/ApiError');

/**
 * Middleware xác thực JSON Web Token
 */
async function verifyJWT(req, res, next) {
  try {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      throw ApiError.unauthorized('Vui lòng đăng nhập để tiếp tục (Thiếu Bearer Token)');
    }

    const token = authHeader.split(' ')[1];
    if (!token) {
      throw ApiError.unauthorized('Token xác thực không hợp lệ');
    }

    let decoded;
    try {
      decoded = jwt.verify(token, env.JWT_SECRET);
    } catch (err) {
      if (err.name === 'TokenExpiredError') {
        throw ApiError.unauthorized('Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại');
      }
      throw ApiError.unauthorized('Token không hợp lệ hoặc đã bị chỉnh sửa');
    }

    // Kiểm tra user còn tồn tại trong database không
    const [rows] = await pool.query(
      'SELECT id, full_name, email, phone, role FROM users WHERE id = ? LIMIT 1',
      [decoded.id]
    );

    if (rows.length === 0) {
      throw ApiError.unauthorized('Tài khoản người dùng không còn tồn tại trong hệ thống');
    }

    // Gán thông tin user vào request
    req.user = rows[0];
    next();
  } catch (error) {
    next(error);
  }
}

/**
 * Middleware kiểm tra phân quyền người dùng (Role-Based Access Control)
 * @param  {...string} allowedRoles Các quyền được phép truy cập (vd: 'admin', 'user')
 */
function requireRole(...allowedRoles) {
  return (req, res, next) => {
    if (!req.user) {
      return next(ApiError.unauthorized('Chưa xác thực người dùng'));
    }

    if (!allowedRoles.includes(req.user.role)) {
      return next(ApiError.forbidden(`Quyền truy cập bị từ chối. Yêu cầu một trong các quyền: ${allowedRoles.join(', ')}`));
    }

    next();
  };
}

module.exports = {
  verifyJWT,
  requireRole
};
