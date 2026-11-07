# STAYHUB - HỆ THỐNG ĐẶT PHÒNG HOMESTAY NGHỈ DƯỠNG

Dự án Đồ án cuối kỳ môn Lập trình Ứng dụng: Hệ thống đặt phòng Homestay nghỉ dưỡng STAYHUB gồm đầy đủ cả Backend API và Android Client Native.

- **Kho lưu trữ GitHub:** https://github.com/nguyenquangtuu/STAYHUB.git

---

## 1. CẤU TRÚC DỰ ÁN

```
STAYHUB/
├── backend/               # RESTful API (NodeJS + Express + MySQL Connection Pool)
│   ├── src/               # Controllers, Models (Raw SQL), Services, Routes, Validators
│   ├── database/          # schema.sql (3NF), seed.sql, initDb.js
│   └── README.md          # Chi tiết tài liệu API & Hướng dẫn backend
│
└── android/               # Android Client App (Java, MVVM, Material 3)
    ├── app/               # Module chính (Retrofit, Glide, ViewBinding, SharedPreferences)
    │   └── src/main/java/com/stayhub/app/
    └── README.md          # Hướng dẫn build, đổi BASE_URL và chạy ứng dụng Android
```

---

## 2. KHỞI CHẠY BACKEND

```bash
cd backend
npm install
cp .env.example .env
npm run db:init
npm run dev
```

Server sẽ lắng nghe tại: `http://localhost:5000` (Health check: `http://localhost:5000/api/health`).

---

## 3. KHỞI CHẠY ANDROID CLIENT

1. Mở **Android Studio**.
2. Chọn **Open** và chọn thư mục `android/`.
3. Chờ Gradle Sync hoàn tất.
4. Chọn máy ảo Android (Emulator) hoặc điện thoại thật.
5. Nhấn **Run** (`Shift + F10`).

> **Lưu ý cấu hình BASE_URL:**
> - Trên máy ảo (Emulator): Sử dụng mặc định `http://10.0.2.2:5000/api/`.
> - Trên điện thoại thật: Mở file `android/app/build.gradle`, sửa `BASE_URL` thành IP LAN của máy tính (ví dụ: `http://192.168.1.X:5000/api/`).
