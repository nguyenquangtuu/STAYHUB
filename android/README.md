# STAYHUB - ANDROID CLIENT APPLICATION

Ứng dụng Android đặt phòng Homestay nghỉ dưỡng **StayHub**, kết nối với hệ thống Backend RESTful API (NodeJS + Express + MySQL).

---

## 1. THÔNG SỐ KỸ THUẬT & KIẾN TRÚC

- **Ngôn ngữ:** Java (chuẩn Android Native)
- **Kiến trúc:** MVVM (Model - View - ViewModel) + Repository Pattern
- **SDK:** `minSdk 24`, `targetSdk 34`, `compileSdk 34`
- **Networking:** Retrofit 2 + OkHttp 4 (Timeout 30s, LoggingInterceptor, AuthInterceptor)
- **Serialization:** Gson
- **Image Loading:** Glide (cache ảnh, bo góc mượt mà)
- **Local Storage:** SharedPreferences (`SessionManager` lưu JWT Token + Profile)
- **UI & Thiết kế:** XML Layout + Material Design 3 (Màu chủ đạo Navy `#1E3A5F` + Cam `#FF8A3D`, Card 12dp corner, 2dp elevation)
- **View Binding:** Kích hoạt `viewBinding true`, không dùng DataBinding phức tạp hay findViewById thủ công.
- **Ngôn ngữ giao diện:** 100% Tiếng Việt.

---

## 2. CẤU TRÚC THƯ MỤC SOURCE CODE

```
android/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/stayhub/app/
│   │   │   ├── StayHubApplication.java       # Quản lý Application Context & Auto-logout 401
│   │   │   ├── data/
│   │   │   │   ├── api/
│   │   │   │   │   ├── ApiClient.java         # Singleton Retrofit + OkHttp
│   │   │   │   │   ├── ApiService.java        # Danh sách RESTful Endpoints
│   │   │   │   │   └── AuthInterceptor.java   # Tự động đính kèm header Bearer Token
│   │   │   │   ├── local/
│   │   │   │   │   └── SessionManager.java    # SharedPreferences lưu JWT & User
│   │   │   │   ├── model/
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Homestay.java
│   │   │   │   │   ├── Room.java
│   │   │   │   │   ├── Booking.java
│   │   │   │   │   ├── Review.java
│   │   │   │   │   ├── ApiResponse.java       # Wrapper response {success, message, data}
│   │   │   │   │   ├── request/               # LoginRequest, RegisterRequest, BookingRequest, ReviewRequest
│   │   │   │   │   └── response/              # AuthResponse, HomestayListResponse, BookingListResponse...
│   │   │   │   └── repository/
│   │   │   │       ├── BaseRepository.java    # Bóc tách lỗi API
│   │   │   │       ├── ApiCallback.java       # Callback chuẩn (onSuccess, onError)
│   │   │   │       ├── AuthRepository.java
│   │   │   │       ├── HomestayRepository.java
│   │   │   │       ├── RoomRepository.java
│   │   │   │       ├── BookingRepository.java
│   │   │   │       └── ReviewRepository.java
│   │   │   ├── ui/
│   │   │   │   ├── SplashActivity.java        # Kiểm tra token -> điều hướng Login hoặc Main
│   │   │   │   ├── auth/
│   │   │   │   │   ├── LoginActivity.java
│   │   │   │   │   └── RegisterActivity.java
│   │   │   │   ├── home/
│   │   │   │   │   ├── MainActivity.java      # Host BottomNavigation (3 tabs)
│   │   │   │   │   ├── HomeFragment.java      # Danh sách homestay + bộ lọc thành phố, giá
│   │   │   │   │   └── HomestayAdapter.java
│   │   │   │   ├── homestay/
│   │   │   │   │   ├── HomestayDetailActivity.java
│   │   │   │   │   └── RoomListAdapter.java
│   │   │   │   ├── booking/
│   │   │   │   │   ├── BookingActivity.java   # DatePicker, kiểm tra phòng trống, tính tổng tiền
│   │   │   │   │   ├── MyBookingsFragment.java# TabLayout: Đang đặt | Lịch sử, Hủy đơn, Đánh giá
│   │   │   │   │   └── BookingAdapter.java
│   │   │   │   ├── review/
│   │   │   │   │   ├── ReviewListAdapter.java
│   │   │   │   │   └── WriteReviewActivity.java# RatingBar 1-5 sao, nhận xét
│   │   │   │   └── profile/
│   │   │   │       └── ProfileFragment.java   # Xem thông tin cá nhân & Đăng xuất
│   │   │   ├── viewmodel/
│   │   │   │   ├── AuthViewModel.java
│   │   │   │   ├── HomestayViewModel.java
│   │   │   │   ├── BookingViewModel.java
│   │   │   │   └── ReviewViewModel.java
│   │   │   └── util/
│   │   │       ├── Constants.java
│   │   │       ├── CurrencyUtils.java         # Format VNĐ (#,### ₫)
│   │   │       ├── DateUtils.java             # Format ngày (dd/MM/yyyy <-> yyyy-MM-dd)
│   │   │       └── UiState.java               # Generic UI State (LOADING, SUCCESS, ERROR)
│   │   └── res/
│   │       ├── layout/                        # 10 layouts màn hình & item hoàn chỉnh
│   │       ├── values/                        # colors.xml, strings.xml, dimens.xml, themes.xml
│   │       ├── values-night/                  # themes.xml (Dark Mode)
│   │       └── xml/network_security_config.xml# Cho phép HTTP cleartext tới 10.0.2.2 & LAN
│   ├── build.gradle                           # App module dependencies & buildConfigField
│   └── proguard-rules.pro
├── build.gradle                               # Root project build file
├── settings.gradle
└── gradle.properties
```

---

## 3. HƯỚNG DẪN KHỞI CHẠY BACKEND LOCAL

Trước khi khởi chạy ứng dụng Android, cần đảm bảo Backend API đang hoạt động:

1. Di chuyển vào thư mục backend:
   ```bash
   cd backend
   ```
2. Cài đặt các gói phụ thuộc:
   ```bash
   npm install
   ```
3. Tạo file cấu hình môi trường `.env` từ `.env.example`:
   ```bash
   cp .env.example .env
   ```
4. Đảm bảo dịch vụ MySQL (XAMPP / WampServer / Docker) đang chạy, sau đó chạy lệnh khởi tạo Database và nạp dữ liệu mẫu:
   ```bash
   npm run db:init
   ```
5. Khởi động Backend API:
   ```bash
   npm run dev
   ```
   Backend sẽ lắng nghe tại: `http://localhost:5000`  
   Endpoint kiểm tra sức khỏe hệ thống: `http://localhost:5000/api/health`

---

## 4. HƯỚNG DẪN CẤU HÌNH BASE_URL

URL kết nối API được cấu hình tập trung trong file `android/app/build.gradle`:

```groovy
defaultConfig {
    ...
    // Mặc định cho Android Emulator
    buildConfigField "String", "BASE_URL", "\"http://10.0.2.2:5000/api/\""
}
```

### Trường hợp 1: Chạy trên Android Emulator (Android Studio)
- Giữ nguyên giá trị mặc định: `"http://10.0.2.2:5000/api/"`.
- `10.0.2.2` là IP loopback đặc biệt của Android Emulator để trỏ tới `localhost` của máy tính.

### Trường hợp 2: Chạy trên Thiết bị thật (Real Device qua Wi-Fi / Cáp USB)
1. Máy tính và điện thoại phải kết nối chung một mạng Wi-Fi.
2. Tìm địa chỉ IP LAN của máy tính bằng lệnh:
   - Trên Windows (PowerShell/CMD): `ipconfig` (Tìm dòng `IPv4 Address`, ví dụ: `192.168.1.15`).
   - Trên macOS/Linux: `ifconfig` hoặc `ip a`.
3. Mở file `android/app/build.gradle` và sửa lại dòng `buildConfigField`:
   ```groovy
   buildConfigField "String", "BASE_URL", "\"http://192.168.1.15:5000/api/\""
   ```
4. Bấm **Sync Project with Gradle Files** trong Android Studio.

---

## 5. HƯỚNG DẪN BUILD VÀ CÀI ĐẶT ỨNG DỤNG

### Cách 1: Mở và chạy trực tiếp bằng Android Studio (Khuyến nghị)
1. Mở **Android Studio**.
2. Chọn **Open** và duyệt tới thư mục `android/` của dự án.
3. Chờ Android Studio đồng bộ Gradle (Gradle Sync) hoàn tất.
4. Chọn thiết bị chạy (Emulator hoặc Thiết bị thật đã bật USB Debugging).
5. Nhấn nút **Run 'app'** (phím tắt `Shift + F10`) để tự động build và cài đặt ứng dụng.

### Cách 2: Build file cài đặt APK
Trong Android Studio:
1. Vào menu **Build** > **Build Bundle(s) / APK(s)** > **Build APK(s)**.
2. Khi hoàn tất, nhấn vào thông báo **locate** ở góc phải màn hình để lấy file APK:
   Đường dẫn file: `android/app/build/outputs/apk/debug/app-debug.apk`.
3. Chép file `app-debug.apk` vào điện thoại và tiến hành cài đặt.

---

## 6. TÀI KHOẢN MẪU ĐÃ CÓ TRONG CƠ SỞ DỮ LIỆU

Mật khẩu mặc định cho tất cả tài khoản là: **123456**

| Email | Mật khẩu | Họ và tên | Vai trò |
| :--- | :--- | :--- | :--- |
| `khachhang1@gmail.com` | `123456` | Nguyễn Văn Khách | Khách hàng (Đã có sẵn 1 đơn COMPLETED để thử nghiệm đánh giá review) |
| `khachhang2@gmail.com` | `123456` | Trần Thị Thu Thảo | Khách hàng (Đã có sẵn 1 đơn CONFIRMED để thử nghiệm kiểm tra trùng lịch và hủy phòng) |
| `admin@stayhub.com` | `123456` | Admin Quản Trị Viên | Quản trị viên hệ thống |

Bạn cũng có thể tự do bấm nút **Đăng ký ngay** trên ứng dụng để tạo tài khoản mới.
