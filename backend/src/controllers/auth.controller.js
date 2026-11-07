const AuthService = require('../services/auth.service');
const { sendSuccess } = require('../utils/response');

/**
 * Controller xử lý các yêu cầu liên quan đến xác thực người dùng
 */
class AuthController {
  /**
   * Đăng ký tài khoản
   */
  static async register(req, res, next) {
    try {
      const { full_name, email, password, phone, role } = req.body;
      const result = await AuthService.register({
        fullName: full_name,
        email,
        password,
        phone,
        role
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
}

module.exports = AuthController;

