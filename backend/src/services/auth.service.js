const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const UserModel = require('../models/user.model');
const ApiError = require('../utils/ApiError');
const env = require('../config/env');

// Bộ nhớ đệm lưu mã OTP tạm thời
const otpCache = new Map();

/**
 * Service xử lý nghiệp vụ Authentication (Đăng ký, Đăng nhập, Profile)
 */
class AuthService {
  /**
   * Chuẩn hóa Identifier (email hoặc phone)
   */
  static normalizeIdentifier(identifier) {
    const raw = (identifier || '').trim();
    if (raw.includes('@')) {
      return { value: raw.toLowerCase(), type: 'email' };
    }
    // Chuẩn hóa số điện thoại Việt Nam (+84 -> 0)
    let phone = raw.replace(/\s+/g, '');
    if (phone.startsWith('+84')) {
      phone = '0' + phone.substring(3);
    }
    return { value: phone, type: 'phone' };
  }

  /**
   * Gửi mã OTP xác thực (Đăng nhập / Đăng ký một chạm)
   */
  static async sendOtp(identifier) {
    const { value, type } = this.normalizeIdentifier(identifier);
    if (!value) {
      throw ApiError.badRequest('Vui lòng cung cấp số điện thoại hoặc email hợp lệ');
    }

    // Kiểm tra xem số điện thoại / email đã tồn tại trong hệ thống chưa
    const existingUser = await UserModel.findByIdentifier(value);
    const isRegistered = !!existingUser;

    // Sinh mã OTP (mẫu cố định 123456 để kiểm thử thuận tiện)
    const mockOtp = '123456';
    otpCache.set(value, {
      otp: mockOtp,
      expiresAt: Date.now() + 10 * 60 * 1000, // 10 phút
      isRegistered
    });

    return {
      identifier: value,
      type,
      is_registered: isRegistered,
      otp_mock: mockOtp
    };
  }

  /**
   * Xác thực mã OTP
   */
  static async verifyOtp({ identifier, otp }) {
    const { value, type } = this.normalizeIdentifier(identifier);
    const cached = otpCache.get(value);

    // Kiểm tra tính hợp lệ của OTP (chấp nhận 123456 hoặc mã trong cache)
    const isValid = otp === '123456' || (cached && cached.otp === otp && cached.expiresAt > Date.now());
    if (!isValid) {
      throw ApiError.badRequest('Mã OTP không chính xác hoặc đã hết hạn');
    }

    // Xóa cache sau khi dùng
    otpCache.delete(value);

    // Kiểm tra tài khoản người dùng
    const user = await UserModel.findByIdentifier(value);
    if (user) {
      // Đã đăng ký -> Đăng nhập luôn và trả về JWT token
      const token = jwt.sign(
        { id: user.id, email: user.email, role: user.role },
        env.JWT_SECRET,
        { expiresIn: env.JWT_EXPIRES_IN }
      );
      const { password_hash, ...userProfile } = user;
      return {
        is_registered: true,
        user: userProfile,
        token
      };
    } else {
      // Chưa đăng ký -> Thông báo bước tiếp theo để điền thông tin
      return {
        is_registered: false,
        identifier: value,
        type
      };
    }
  }

  /**
   * Hoàn tất đăng ký tài khoản mới sau khi xác thực OTP thành công
   */
  static async completeRegistration({ fullName, email, phone, password = 'StayHubUser@2026' }) {
    const normEmail = (email || '').trim().toLowerCase();
    const normPhone = (phone || '').trim().replace('+84', '0');

    // 1. Kiểm tra trùng lặp email
    const existingEmail = await UserModel.findByEmail(normEmail);
    if (existingEmail) {
      throw ApiError.conflict('Email này đã được sử dụng bởi một tài khoản khác');
    }

    // 2. Kiểm tra trùng lặp số điện thoại
    if (normPhone) {
      const existingPhone = await UserModel.findByPhone(normPhone);
      if (existingPhone) {
        throw ApiError.conflict('Số điện thoại này đã được sử dụng bởi tài khoản khác');
      }
    }

    // 3. Tạo mật khẩu hash
    const saltRounds = 10;
    const passwordHash = await bcrypt.hash(password, saltRounds);

    // 4. Lưu vào cơ sở dữ liệu
    const newUserId = await UserModel.create({
      fullName: fullName.trim(),
      email: normEmail,
      passwordHash,
      phone: normPhone || null,
      role: 'user'
    });

    const newUser = await UserModel.findById(newUserId);

    // 5. Sinh JWT token
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
   * Đăng ký tài khoản mới cho người dùng
   * Lưu ý an mật: Endpoint đăng ký công khai LUÔN LUÔN gán role = 'user',
   * tuyệt đối không nhận role từ client để triệt tiêu hoàn toàn lỗ hổng Privilege Escalation.
   */
  static async register({ fullName, email, password, phone = null }) {
    // 1. Kiểm tra email đã được đăng ký chưa
    const existingUser = await UserModel.findByEmail(email);
    if (existingUser) {
      throw ApiError.conflict('Email này đã được sử dụng bởi một tài khoản khác');
    }

    // 2. Hash mật khẩu bằng bcrypt (10 rounds)
    const saltRounds = 10;
    const passwordHash = await bcrypt.hash(password, saltRounds);

    // 3. Lưu vào cơ sở dữ liệu (role mặc định luôn luôn là 'user')
    const newUserId = await UserModel.create({
      fullName,
      email,
      passwordHash,
      phone,
      role: 'user'
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

