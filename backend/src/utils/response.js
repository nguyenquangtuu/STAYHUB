/**
 * Helper chuẩn hóa định dạng phản hồi API theo quy ước
 */

/**
 * Trả về response thành công
 * @param {import('express').Response} res
 * @param {any} data Dữ liệu trả về
 * @param {string} message Thông điệp thành công
 * @param {number} statusCode HTTP Status Code (mặc định 200)
 */
function sendSuccess(res, data = null, message = 'Thành công', statusCode = 200) {
  return res.status(statusCode).json({
    success: true,
    message,
    data
  });
}

/**
 * Trả về response lỗi
 * @param {import('express').Response} res
 * @param {string|number} code Mã lỗi nội bộ hoặc HTTP Status Code
 * @param {string} message Thông báo lỗi
 * @param {any} details Chi tiết lỗi bổ sung (nếu có)
 * @param {number} statusCode HTTP Status Code (mặc định 500)
 */
function sendError(res, code = 500, message = 'Đã có lỗi xảy ra', details = null, statusCode = 500) {
  const responsePayload = {
    success: false,
    error: {
      code,
      message
    }
  };

  if (details !== null && details !== undefined) {
    responsePayload.error.details = details;
  }

  return res.status(statusCode).json(responsePayload);
}

module.exports = {
  sendSuccess,
  sendError
};

