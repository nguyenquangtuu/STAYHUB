const { body } = require('express-validator');

// Biểu thức chính quy kiểm tra số điện thoại Việt Nam chuẩn (10 số, đầu số 03, 05, 07, 08, 09 hoặc +84)
const VN_PHONE_REGEX = /^(0|\+84)(3|5|7|8|9)[0-9]{8}$/;

const sendOtpValidator = [
  body('identifier')
    .trim()
    .notEmpty().withMessage('Vui lòng nhập số điện thoại hoặc email')
    .custom((val) => {
      const isEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val);
      const isPhone = VN_PHONE_REGEX.test(val.replace(/\s+/g, ''));
      if (!isEmail && !isPhone) {
        throw new Error('Vui lòng nhập email hợp lệ hoặc số điện thoại Việt Nam 10 chữ số (VD: 0912345678)');
      }
      return true;
    })
];

const verifyOtpValidator = [
  body('identifier')
    .trim()
    .notEmpty().withMessage('Vui lòng cung cấp số điện thoại hoặc email'),
  body('otp')
    .trim()
    .notEmpty().withMessage('Vui lòng nhập mã OTP')
    .matches(/^[0-9]{6}$/).withMessage('Mã OTP phải bao gồm đúng 6 chữ số')
];

const completeRegistrationValidator = [
  body('full_name')
    .trim()
    .notEmpty().withMessage('Họ và tên không được để trống')
    .isLength({ min: 2, max: 100 }).withMessage('Họ và tên phải từ 2 đến 100 ký tự'),

  body('email')
    .trim()
    .notEmpty().withMessage('Email không được để trống')
    .isEmail().withMessage('Địa chỉ email không đúng định dạng')
    .normalizeEmail(),

  body('phone')
    .optional({ nullable: true, checkFalsy: true })
    .trim()
    .custom((val) => {
      if (val && !VN_PHONE_REGEX.test(val.replace(/\s+/g, ''))) {
        throw new Error('Số điện thoại không đúng định dạng Việt Nam (10 chữ số, VD: 0912345678)');
      }
      return true;
    }),

  body('role')
    .custom((val) => {
      if (val !== undefined && val !== null) {
        throw new Error('Không được phép tự chỉ định quyền hạn (role) trong yêu cầu đăng ký');
      }
      return true;
    })
];

const updateProfileValidator = [
  body('full_name')
    .optional()
    .trim()
    .isLength({ min: 2, max: 100 }).withMessage('Họ và tên phải từ 2 đến 100 ký tự'),

  body('phone')
    .optional({ nullable: true, checkFalsy: true })
    .trim()
    .custom((val) => {
      if (val && !VN_PHONE_REGEX.test(val.replace(/\s+/g, ''))) {
        throw new Error('Số điện thoại không đúng định dạng Việt Nam (10 chữ số, VD: 0912345678)');
      }
      return true;
    }),

  body('role')
    .custom((val) => {
      if (val !== undefined && val !== null) {
        throw new Error('Không được phép tự chỉ định quyền hạn (role) trong yêu cầu đăng ký');
      }
      return true;
    })
];

const registerValidator = [
  body('full_name')
    .trim()
    .notEmpty().withMessage('Họ và tên không được để trống')
    .isLength({ min: 2, max: 100 }).withMessage('Họ và tên phải từ 2 đến 100 ký tự'),

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
    .custom((val) => {
      if (val && !VN_PHONE_REGEX.test(val.replace(/\s+/g, ''))) {
        throw new Error('Số điện thoại không đúng định dạng Việt Nam (10 chữ số, VD: 0912345678)');
      }
      return true;
    }),

  body('role')
    .custom((val) => {
      if (val !== undefined && val !== null) {
        throw new Error('Không được phép tự chỉ định quyền hạn (role) trong yêu cầu đăng ký');
      }
      return true;
    })
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
  sendOtpValidator,
  verifyOtpValidator,
  completeRegistrationValidator,
  updateProfileValidator,
  registerValidator,
  loginValidator
};

