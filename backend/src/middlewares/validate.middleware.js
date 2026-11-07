const { validationResult } = require('express-validator');
const ApiError = require('../utils/ApiError');

/**
 * Middleware tổng hợp và kiểm tra kết quả validation từ express-validator
 */
function validateRequest(req, res, next) {
  const errors = validationResult(req);
  if (errors.isEmpty()) {
    return next();
  }

  // Chuẩn hóa định dạng lỗi trả về
  const formattedErrors = errors.array().map((err) => ({
    field: err.path || err.param,
    message: err.msg,
    value: err.value
  }));

  const firstErrorMessage = formattedErrors[0]?.message || 'Dữ liệu không hợp lệ';
  return next(ApiError.badRequest(firstErrorMessage, formattedErrors));
}

module.exports = {
  validateRequest
};
