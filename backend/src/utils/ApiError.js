/**
 * Custom Error Class kế thừa Error tiêu chuẩn
 * Dùng để ném lỗi có kèm HTTP status code và details trong toàn bộ hệ thống
 */
class ApiError extends Error {
  /**
   * @param {number} statusCode HTTP Status code
   * @param {string} message Thông báo lỗi
   * @param {any} details Chi tiết lỗi bổ sung (ví dụ mảng validate error)
   */
  constructor(statusCode, message, details = null) {
    super(message);
    this.statusCode = statusCode;
    this.details = details;
    Error.captureStackTrace(this, this.constructor);
  }

  static badRequest(message = 'Dữ liệu yêu cầu không hợp lệ', details = null) {
    return new ApiError(400, message, details);
  }

  static unauthorized(message = 'Chưa xác thực hoặc token không hợp lệ', details = null) {
    return new ApiError(401, message, details);
  }

  static forbidden(message = 'Bạn không có quyền thực hiện hành động này', details = null) {
    return new ApiError(403, message, details);
  }

  static notFound(message = 'Không tìm thấy tài nguyên yêu cầu', details = null) {
    return new ApiError(404, message, details);
  }

  static conflict(message = 'Dữ liệu bị trùng lặp hoặc xung đột trạng thái', details = null) {
    return new ApiError(409, message, details);
  }

  static internal(message = 'Lỗi máy chủ nội bộ', details = null) {
    return new ApiError(500, message, details);
  }
}

module.exports = ApiError;
