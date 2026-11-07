/**
 * Helper phân trang chuẩn hóa cho danh sách kết quả truy vấn
 */

/**
 * Xử lý query params page & limit
 * @param {object} query - req.query
 * @param {number} defaultLimit - số item mỗi trang mặc định
 * @returns {{ page: number, limit: number, offset: number }}
 */
function getPaginationParams(query, defaultLimit = 10) {
  const page = Math.max(1, parseInt(query.page, 10) || 1);
  const limit = Math.max(1, Math.min(100, parseInt(query.limit, 10) || defaultLimit));
  const offset = (page - 1) * limit;

  return { page, limit, offset };
}

/**
 * Tạo metadata phản hồi phân trang
 * @param {number} totalItems - tổng số bản ghi
 * @param {number} page - trang hiện tại
 * @param {number} limit - số lượng mỗi trang
 * @returns {{ totalItems: number, totalPages: number, currentPage: number, limit: number }}
 */
function buildPaginationMeta(totalItems, page, limit) {
  const totalPages = Math.ceil(totalItems / limit) || 1;
  return {
    totalItems,
    totalPages,
    currentPage: page,
    limit
  };
}

module.exports = {
  getPaginationParams,
  buildPaginationMeta
};
