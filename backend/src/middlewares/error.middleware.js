const ApiError = require('../utils/ApiError');
const { sendError } = require('../utils/response');
const env = require('../config/env');

/**
 * Global Error Handler Middleware
 * Bắt mọi lỗi xảy ra trong ứng dụng và chuyển đổi thành format JSON thống nhất
 */
function errorHandler(err, req, res, next) {
  // In lỗi ra terminal khi ở môi trường development
  if (env.NODE_ENV === 'development') {
    console.error('[Error Details]:', err);
  }

  // 1. Trường hợp là ApiError tùy biến
  if (err instanceof ApiError) {
    return sendError(
      res,
      err.statusCode,
      err.message,
      err.details,
      err.statusCode
    );
  }

  // 2. Xử lý lỗi MySQL trùng lặp (Duplicate Key / Unique Constraint)
  if (err.code === 'ER_DUP_ENTRY') {
    let message = 'Dữ liệu này đã tồn tại trong hệ thống';
    if (err.sqlMessage && err.sqlMessage.includes('email')) {
      message = 'Email này đã được sử dụng';
    } else if (err.sqlMessage && err.sqlMessage.includes('booking_id')) {
      message = 'Mỗi đơn đặt phòng chỉ được đánh giá một lần duy nhất';
    }
    return sendError(res, 409, message, null, 409);
  }

  // 3. Xử lý lỗi MySQL Khóa ngoại tham chiếu không tồn tại
  if (err.code === 'ER_NO_REFERENCED_ROW_2') {
    return sendError(res, 400, 'Dữ liệu liên kết (ID tham chiếu) không tồn tại', null, 400);
  }

  // 4. Xử lý lỗi MySQL Khóa ngoại không cho phép xóa
  if (err.code === 'ER_ROW_IS_REFERENCED_2') {
    return sendError(res, 400, 'Không thể xóa dữ liệu vì đang có liên kết phụ thuộc', null, 400);
  }

  // 5. Xử lý lỗi cú pháp JSON từ client
  if (err instanceof SyntaxError && err.status === 400 && 'body' in err) {
    return sendError(res, 400, 'Cú pháp body JSON không hợp lệ', null, 400);
  }

  // 6. Lỗi hệ thống mặc định 500
  const message = env.NODE_ENV === 'production' 
    ? 'Đã xảy ra lỗi hệ thống, vui lòng thử lại sau' 
    : (err.message || 'Lỗi máy chủ nội bộ');

  return sendError(res, 500, message, null, 500);
}

/**
 * Middleware bắt các route không tồn tại (404)
 */
function notFoundHandler(req, res, next) {
  next(ApiError.notFound(`Tuyến đường ${req.method} ${req.originalUrl} không tồn tại trên hệ thống`));
}

module.exports = {
  errorHandler,
  notFoundHandler
};

