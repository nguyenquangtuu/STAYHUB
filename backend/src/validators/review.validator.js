const { body } = require('express-validator');

const createReviewValidator = [
  body('booking_id')
    .notEmpty().withMessage('Mã đơn đặt phòng (booking_id) là bắt buộc')
    .isInt({ min: 1 }).withMessage('booking_id phải là số nguyên dương'),

  body('rating')
    .notEmpty().withMessage('Điểm đánh giá (rating) là bắt buộc')
    .isInt({ min: 1, max: 5 }).withMessage('Điểm đánh giá phải từ 1 đến 5 sao'),

  body('comment')
    .optional({ nullable: true })
    .trim()
    .isLength({ max: 1000 }).withMessage('Bình luận không được vượt quá 1000 ký tự')
];

module.exports = {
  createReviewValidator
};
