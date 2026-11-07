const { body } = require('express-validator');

const createReviewValidator = [
  body('booking_id')
    .custom((val, { req }) => {
      const id = val || req.body.bookingId;
      if (!id || !Number.isInteger(Number(id)) || Number(id) < 1) {
        throw new Error('Mã đơn đặt phòng (booking_id hoặc bookingId) là bắt buộc và phải là số nguyên dương');
      }
      return true;
    }),

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
