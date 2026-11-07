/**
 * Các hàm tiện ích xử lý ngày tháng cho hệ thống Booking
 */

/**
 * Format Date object thành chuỗi YYYY-MM-DD
 * @param {Date|string} date
 * @returns {string}
 */
function formatDateToYYYYMMDD(date) {
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

/**
 * Lấy ngày hôm nay dưới dạng YYYY-MM-DD (00:00:00 địa phương)
 * @returns {Date}
 */
function getTodayStart() {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return today;
}

/**
 * Tính số đêm giữa 2 ngày check_in và check_out
 * @param {string|Date} checkIn
 * @param {string|Date} checkOut
 * @returns {number}
 */
function calculateNights(checkIn, checkOut) {
  const start = new Date(checkIn);
  const end = new Date(checkOut);
  
  // Xóa phần giờ phút giây để tính chẵn ngày
  start.setHours(0, 0, 0, 0);
  end.setHours(0, 0, 0, 0);

  const diffTime = end.getTime() - start.getTime();
  const diffDays = Math.round(diffTime / (1000 * 60 * 60 * 24));
  return diffDays;
}

/**
 * Kiểm tra ngày check_in có lớn hơn hoặc bằng hôm nay hay không
 * @param {string|Date} checkIn
 * @returns {boolean}
 */
function isCheckInFromToday(checkIn) {
  const start = new Date(checkIn);
  start.setHours(0, 0, 0, 0);
  const today = getTodayStart();
  return start.getTime() >= today.getTime();
}

/**
 * Kiểm tra xem có đủ điều kiện hủy phòng (ít nhất 24 giờ trước giờ check_in tiêu chuẩn 14:00)
 * @param {string|Date} checkInDate
 * @returns {boolean}
 */
function canCancelBooking(checkInDate) {
  // Giả định giờ nhận phòng tiêu chuẩn của homestay là 14:00 ngày check_in
  const checkInDateTime = new Date(checkInDate);
  // Nếu truyền dạng YYYY-MM-DD, set vào 14h chiều của ngày đó
  const year = checkInDateTime.getFullYear();
  const month = checkInDateTime.getMonth();
  const day = checkInDateTime.getDate();
  const checkInDeadline = new Date(year, month, day, 14, 0, 0);

  const now = new Date();
  const hoursDifference = (checkInDeadline.getTime() - now.getTime()) / (1000 * 60 * 60);

  return hoursDifference >= 24;
}

module.exports = {
  formatDateToYYYYMMDD,
  calculateNights,
  isCheckInFromToday,
  canCancelBooking
};
