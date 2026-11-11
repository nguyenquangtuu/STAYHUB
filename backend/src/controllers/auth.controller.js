const AuthService = require('../services/auth.service');
const { sendSuccess } = require('../utils/response');

/**
 * Controller xử lý các yêu cầu liên quan đến xác thực người dùng
 */
class AuthController {
  /**
   * Gửi mã OTP xác thực (Đăng nhập / Đăng ký một chạm)
   */
  static async sendOtp(req, res, next) {
    try {
      const { identifier } = req.body;
      const result = await AuthService.sendOtp(identifier);
      const actionText = result.is_registered ? 'đăng nhập' : 'đăng ký';
      return sendSuccess(res, result, `Mã OTP xác thực ${actionText} đã được gửi thành công`, 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Xác thực mã OTP
   */
  static async verifyOtp(req, res, next) {
    try {
      const { identifier, otp } = req.body;
      const result = await AuthService.verifyOtp({ identifier, otp });
      const msg = result.is_registered 
        ? 'Đăng nhập thành công' 
        : 'Xác thực OTP thành công. Vui lòng hoàn tất thông tin tài khoản';
      return sendSuccess(res, result, msg, 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Hoàn tất thông tin đăng ký tài khoản
   */
  static async completeRegistration(req, res, next) {
    try {
      const { full_name, email, phone, password } = req.body;
      const result = await AuthService.completeRegistration({
        fullName: full_name,
        email,
        phone,
        password
      });
      return sendSuccess(res, result, 'Đăng ký tài khoản thành công', 201);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Đăng ký tài khoản người dùng
   * (Chỉ trích xuất các trường hợp lệ, ngăn chặn Mass Assignment và leo thang đặc quyền)
   */
  static async register(req, res, next) {
    try {
      const { full_name, email, password, phone } = req.body;
      const result = await AuthService.register({
        fullName: full_name,
        email,
        password,
        phone
      });
      return sendSuccess(res, result, 'Đăng ký tài khoản thành công', 201);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Đăng nhập
   */
  static async login(req, res, next) {
    try {
      const { email, password } = req.body;
      const result = await AuthService.login({ email, password });
      return sendSuccess(res, result, 'Đăng nhập thành công', 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Lấy thông tin tài khoản hiện tại
   */
  static async getMe(req, res, next) {
    try {
      const user = await AuthService.getMe(req.user.id);
      return sendSuccess(res, user, 'Lấy thông tin tài khoản thành công', 200);
    } catch (error) {
      next(error);
    }
  }

  /**
   * Cập nhật thông tin tài khoản hiện tại
   */
  static async updateProfile(req, res, next) {
    try {
      const { full_name, phone } = req.body;
      const user = await AuthService.updateProfile(req.user.id, {
        fullName: full_name,
        phone
      });
      return sendSuccess(res, user, 'Cập nhật thông tin tài khoản thành công', 200);
    } catch (error) {
      next(error);
    }
  }
}

module.exports = AuthController;

