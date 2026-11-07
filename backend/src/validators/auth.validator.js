const { body } = require('express-validator');

const registerValidator = [
  body('full_name')
    .trim()
    .notEmpty().withMessage('Họ và tên không được để trống')
    .isLength({ max: 100 }).withMessage('Họ và tên không được vượt quá 100 ký tự'),

  body('email')
    .trim()
    .notEmpty().withMessage('Email không được để trống')
    .isEmail().withMessage('Địa chỉ email không đúng định dạng')
    .normalizeEmail(),

  body('password')
    .notEmpty().withMessage('Mật khẩu không được để trống')
    .isLength({ min: 6 }).withMessage('Mật khẩu phải có ít nhất 6 ký tự'),

  body('phone')
    .optional({ nullable: true, checkFalsy: true })
    .trim()
    .matches(/^[0-9+]{9,15}$/).withMessage('Số điện thoại không hợp lệ (9-15 số)'),

  body('role')
    .optional()
    .isIn(['user', 'admin']).withMessage('Quyền hạn chỉ có thể là user hoặc admin')
];

const loginValidator = [
  body('email')
    .trim()
    .notEmpty().withMessage('Email không được để trống')
    .isEmail().withMessage('Email không đúng định dạng')
    .normalizeEmail(),

  body('password')
    .notEmpty().withMessage('Mật khẩu không được để trống')
];

module.exports = {
  registerValidator,
  loginValidator
};
