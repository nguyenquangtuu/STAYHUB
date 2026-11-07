const { body, param } = require('express-validator');

const createBookingValidator = [
  body('room_id')
    .notEmpty().withMessage('Mã phòng (room_id) là bắt buộc')
    .isInt({ min: 1 }).withMessage('room_id phải là số nguyên dương'),

  body('check_in')
    .notEmpty().withMessage('Ngày nhận phòng (check_in) là bắt buộc')
    .matches(/^\d{4}-\d{2}-\d{2}$/).withMessage('check_in phải có định dạng YYYY-MM-DD'),

  body('check_out')
    .notEmpty().withMessage('Ngày trả phòng (check_out) là bắt buộc')
    .matches(/^\d{4}-\d{2}-\d{2}$/).withMessage('check_out phải có định dạng YYYY-MM-DD'),

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
