const { body, param } = require('express-validator');

const createRoomValidator = [
  param('id')
    .isInt({ min: 1 }).withMessage('ID homestay không hợp lệ'),

  body('name')
    .trim()
    .notEmpty().withMessage('Tên phòng không được để trống')
    .isLength({ max: 100 }).withMessage('Tên phòng không quá 100 ký tự'),

  body('price_per_night')
    .notEmpty().withMessage('Giá mỗi đêm (price_per_night) là bắt buộc')
    .isFloat({ min: 0 }).withMessage('Giá mỗi đêm phải là số dương'),

  body('capacity')
    .notEmpty().withMessage('Sức chứa tối đa (capacity) là bắt buộc')
    .isInt({ min: 1 }).withMessage('Sức chứa phải từ 1 người trở lên'),

  body('bed_type')
    .trim()
    .notEmpty().withMessage('Loại giường (bed_type) là bắt buộc')
    .isLength({ max: 50 }).withMessage('Loại giường không quá 50 ký tự'),

  body('status')
    .optional()
    .isIn(['available', 'maintenance']).withMessage('Trạng thái phòng phải là available hoặc maintenance')
];

const updateRoomValidator = [
  param('id')
    .isInt({ min: 1 }).withMessage('ID phòng không hợp lệ'),

  body('name')
    .optional()
    .trim()
    .notEmpty().withMessage('Tên phòng không được để trống'),

  body('price_per_night')
    .optional()
    .isFloat({ min: 0 }).withMessage('Giá phòng phải là số dương'),

  body('capacity')
    .optional()
    .isInt({ min: 1 }).withMessage('Sức chứa phải từ 1 người trở lên'),

  body('bed_type')
    .optional()
    .trim()
    .notEmpty().withMessage('Loại giường không được để trống'),

  body('status')
    .optional()
    .isIn(['available', 'maintenance']).withMessage('Trạng thái phòng phải là available hoặc maintenance')
];

module.exports = {
  createRoomValidator,
  updateRoomValidator
};

