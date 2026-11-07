const { body, param } = require('express-validator');

const createHomestayValidator = [
  body('name')
    .trim()
    .notEmpty().withMessage('Tên homestay không được để trống')
    .isLength({ max: 200 }).withMessage('Tên homestay không được quá 200 ký tự'),

  body('city')
    .trim()
    .notEmpty().withMessage('Thành phố không được để trống')
    .isLength({ max: 100 }).withMessage('Tên thành phố không được quá 100 ký tự'),

  body('address')
    .trim()
    .notEmpty().withMessage('Địa chỉ chi tiết không được để trống')
    .isLength({ max: 255 }).withMessage('Địa chỉ không được quá 255 ký tự'),

  body('description')
    .optional({ nullable: true })
    .trim(),

  body('amenities')
    .optional()
    .isArray().withMessage('Tiện ích (amenities) phải là một danh sách mảng JSON (ví dụ: ["wifi", "pool"])')
];

const updateHomestayValidator = [
  param('id')
    .isInt({ min: 1 }).withMessage('ID homestay không hợp lệ'),

  body('name')
    .optional()
    .trim()
    .notEmpty().withMessage('Tên homestay không được để trống'),

  body('city')
    .optional()
    .trim()
    .notEmpty().withMessage('Thành phố không được để trống'),

  body('address')
    .optional()
    .trim()
    .notEmpty().withMessage('Địa chỉ không được để trống'),

  body('amenities')
    .optional()
    .isArray().withMessage('Tiện ích phải là một danh sách mảng JSON')
];

module.exports = {
  createHomestayValidator,
  updateHomestayValidator
};

