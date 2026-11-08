const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const UserModel = require('../models/user.model');
const ApiError = require('../utils/ApiError');
const env = require('../config/env');

/**
 * Service xử lý nghiệp vụ Authentication (Đăng ký, Đăng nhập, Profile)
 */
class AuthService {
  /**
   * Đăng ký tài khoản mới
   */
  static async register({ fullName, email, password, phone = null, role = 'user' }) {
    // 1. Kiểm tra email đã được đăng ký chưa
    const existingUser = await UserModel.findByEmail(email);
    if (existingUser) {
      throw ApiError.conflict('Email này đã được sử dụng bởi một tài khoản khác');
    }

    // 2. Hash mật khẩu bằng bcrypt (10 rounds)
    const saltRounds = 10;
    const passwordHash = await bcrypt.hash(password, saltRounds);

    // 3. Lưu vào cơ sở dữ liệu
    const newUserId = await UserModel.create({
      fullName,
      email,
      passwordHash,
      phone,
      role
    });

    // 4. Lấy thông tin user vừa tạo (không bao gồm password)
    const newUser = await UserModel.findById(newUserId);

    // 5. Tạo JSON Web Token
    const token = jwt.sign(
      { id: newUser.id, email: newUser.email, role: newUser.role },
      env.JWT_SECRET,
      { expiresIn: env.JWT_EXPIRES_IN }
    );

    return {
      user: newUser,
      token
    };
  }

  /**
   * Đăng nhập người dùng
   */
  static async login({ email, password }) {
    // 1. Tìm user theo email
    const user = await UserModel.findByEmail(email);
    if (!user) {
      throw ApiError.unauthorized('Email hoặc mật khẩu không chính xác');
    }

    // 2. So khớp mật khẩu với password_hash
    const isMatch = await bcrypt.compare(password, user.password_hash);
    if (!isMatch) {
      throw ApiError.unauthorized('Email hoặc mật khẩu không chính xác');
    }

    // 3. Tạo JWT token
    const token = jwt.sign(
      { id: user.id, email: user.email, role: user.role },
      env.JWT_SECRET,
      { expiresIn: env.JWT_EXPIRES_IN }
    );

    // Loại bỏ password_hash trước khi trả về
    const { password_hash, ...userProfile } = user;

    return {
      user: userProfile,
      token
    };
  }

  /**
   * Lấy thông tin tài khoản hiện tại (me)
   */
  static async getMe(userId) {
    const user = await UserModel.findById(userId);
    if (!user) {
      throw ApiError.notFound('Không tìm thấy thông tin tài khoản');
    }
    return user;
  }

  /**
   * Cập nhật thông tin tài khoản hiện tại (Họ tên, SĐT)
   */
  static async updateProfile(userId, { fullName, phone }) {
    await UserModel.update(userId, { fullName, phone });
    const user = await UserModel.findById(userId);
    if (!user) {
      throw ApiError.notFound('Không tìm thấy thông tin tài khoản');
    }
    return user;
  }
}

module.exports = AuthService;

