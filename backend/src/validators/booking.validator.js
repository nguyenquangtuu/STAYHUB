const { body, param } = require('express-validator');

const createBookingValidator = [
  body('room_id')
    .custom((val, { req }) => {
      const id = val || req.body.roomId;
      if (!id || !Number.isInteger(Number(id)) || Number(id) < 1) {
        throw new Error('Mã phòng (room_id hoặc roomId) là bắt buộc và phải là số nguyên dương');
      }
      return true;
    }),

  body('check_in')
    .custom((val, { req }) => {
      const date = val || req.body.checkIn;
      if (!date || !/^\d{4}-\d{2}-\d{2}$/.test(date)) {
        throw new Error('Ngày nhận phòng (check_in hoặc checkIn) là bắt buộc, định dạng YYYY-MM-DD');
      }
      return true;
    }),

  body('check_out')
    .custom((val, { req }) => {
      const date = val || req.body.checkOut;
      if (!date || !/^\d{4}-\d{2}-\d{2}$/.test(date)) {
        throw new Error('Ngày trả phòng (check_out hoặc checkOut) là bắt buộc, định dạng YYYY-MM-DD');
      }
      return true;
    }),

  body('guests')
    .notEmpty().withMessage('Số lượng khách (guests) là bắt buộc')
    .isInt({ min: 1 }).withMessage('Số lượng khách phải lớn hơn hoặc bằng 1'),

  body('note')
    .optional({ nullable: true })
    .trim()
    .isLength({ max: 500 }).withMessage('Ghi chú không được dài quá 500 ký tự')
];

const updateBookingStatusValidator = [
  param('id')
    .isInt({ min: 1 }).withMessage('ID booking không hợp lệ'),

  body('status')
    .notEmpty().withMessage('Trạng thái mới là bắt buộc')
    .isIn(['CONFIRMED', 'REJECTED', 'COMPLETED']).withMessage('Trạng thái hợp lệ gồm: CONFIRMED, REJECTED, COMPLETED')
];

module.exports = {
  createBookingValidator,
  updateBookingStatusValidator
};
