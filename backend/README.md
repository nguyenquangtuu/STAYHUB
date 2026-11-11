# STAYHUB - BACKEND RESTFUL API

Hệ thống Backend API cho ứng dụng đặt phòng Homestay nghỉ dưỡng STAYHUB, được xây dựng bằng NodeJS + Express + MySQL theo mô hình kiến trúc phân lớp chuẩn công nghiệp (3-Tier Architecture), tối ưu hóa hiệu năng, xử lý đồng thời (Concurrency Control) và đáp ứng đầy đủ các tiêu chuẩn đồ án tốt nghiệp, đồ án chuyên ngành CNTT.

---

## 1. TECH STACK VÀ QUY ƯỚC CÔNG NGHỆ

- Runtime và Ngôn ngữ: Node.js v18+ (CommonJS)
- Web Framework: Express.js
- Hệ quản trị CSDL: MySQL 8.0+ / MariaDB
- Database Driver: mysql2/promise (Sử dụng Connection Pool, Raw SQL với Prepared Statements để tối ưu hiệu năng và phòng chống SQL Injection, không dùng ORM)
- Bảo mật:
  - bcryptjs: Băm mật khẩu (10 salt rounds)
  - jsonwebtoken (JWT): Xác thực phiên làm việc Stateless
  - helmet: Tăng cường bảo mật HTTP Headers
  - cors: Cấu hình chia sẻ tài nguyên nguồn gốc chéo
- Kiểm tra dữ liệu đầu vào: express-validator
- Logging: morgan

---

## 2. CẤU TRÚC THƯ MỤC DỰ ÁN

```
backend/
├── src/
│   ├── config/
│   │   ├── database.js          # Khởi tạo MySQL Connection Pool và kiểm tra kết nối
│   │   └── env.js               # Load và kiểm soát biến môi trường từ file .env
│   ├── models/                  # Tầng thao tác CSDL thuần (Raw SQL Prepared Statements)
│   │   ├── user.model.js
│   │   ├── homestay.model.js    # Tìm kiếm động (thành phố, giá, số khách, tiện ích)
│   │   ├── room.model.js
│   │   ├── booking.model.js     # Query kiểm tra trùng lịch và khóa dòng FOR UPDATE
│   │   └── review.model.js      # Lưu review và tính lại điểm avg_rating
│   ├── controllers/             # Tiếp nhận request HTTP, gọi service và trả response
│   │   ├── auth.controller.js
│   │   ├── homestay.controller.js
│   │   ├── room.controller.js
│   │   ├── booking.controller.js
│   │   └── review.controller.js
│   ├── services/                # Tầng nghiệp vụ cốt lõi (Business Logic & Transactions)
│   │   ├── auth.service.js
│   │   ├── homestay.service.js
│   │   ├── room.service.js
│   │   ├── booking.service.js   # ACID Transaction đặt phòng, hủy phòng >= 24h
│   │   └── review.service.js    # Kiểm tra booking COMPLETED và cập nhật avg_rating
│   ├── routes/                  # Định nghĩa endpoints và gắn middlewares
│   │   ├── index.js             # Gom cụm tất cả route về tiền tố /api
│   │   ├── auth.routes.js
│   │   ├── homestay.routes.js
│   │   ├── room.routes.js
│   │   ├── booking.routes.js
│   │   └── review.routes.js
│   ├── middlewares/             # Các bộ lọc trung gian
│   │   ├── auth.middleware.js   # verifyJWT, requireRole('user', 'admin')
│   │   ├── validate.middleware.js # Bắt lỗi từ express-validator
│   │   └── error.middleware.js  # Global Error Handler chuẩn hóa lỗi
│   ├── validators/              # Định nghĩa quy tắc validate body/param/query
│   │   ├── auth.validator.js
│   │   ├── homestay.validator.js
│   │   ├── room.validator.js
│   │   ├── booking.validator.js
│   │   └── review.validator.js
│   ├── utils/                   # Hàm tiện ích dùng chung
│   │   ├── response.js          # Format JSON response thống nhất
│   │   ├── ApiError.js          # Custom Error Class chuẩn HTTP status
│   │   ├── dateHelper.js        # Tính số đêm, kiểm tra điều kiện hủy 24h
│   │   └── pagination.js        # Helper phân trang chuẩn
│   └── app.js                   # Cấu hình Express App
├── database/
│   ├── initDb.js                # Script tự động đọc cấu hình từ .env để tạo DB và import SQL
│   ├── schema.sql               # Kịch bản tạo Database, Tables 3NF, Foreign Keys & Indexes
│   └── seed.sql                 # Dữ liệu mẫu (1 Admin, 2 User, 3 Homestay, 6 Room)
├── .env.example                 # Mẫu cấu hình biến môi trường
├── .gitignore
├── package.json
├── server.js                    # File khởi động Server
└── README.md
```

---

## 3. HƯỚNG DẪN CÀI ĐẶT VÀ KHỞI CHẠY

### Cách 1: Khởi chạy siêu tốc bằng Docker (Khuyến nghị)
Hệ thống đã được đóng gói hoàn chỉnh bằng Docker Compose (gồm MySQL 8.0 tự động khởi tạo database + nạp sẵn dữ liệu mẫu, và Node.js API):

1. **Khởi động bằng file script tiện ích (Windows):**
   Chỉ cần nhấp đúp vào file `run_backend_docker.bat` ở thư mục gốc.

2. **Hoặc chạy bằng lệnh Docker Compose:**
   ```bash
   # Tại thư mục backend/ hoặc thư mục gốc:
   docker compose up -d --build
   ```
   - API lắng nghe tại: `http://localhost:5000`
   - Health check: `http://localhost:5000/api/health`
   - MySQL lắng nghe tại: `localhost:3306`
   - Dừng dịch vụ: `docker compose down`
   - Xem logs: `docker compose logs -f`

---

### Cách 2: Khởi chạy thủ công bằng NodeJS & MySQL cục bộ
Di chuyển vào thư mục backend và chạy lệnh:
```bash
cd backend
npm install
```

### Bước 2: Cấu hình biến môi trường (.env)
Toàn bộ thông tin cấu hình kết nối CSDL được đặt tập trung trong file `.env` nằm bên trong thư mục `backend/`.
Tạo file `backend/.env` từ file `backend/.env.example`:
```bash
cp .env.example .env
```
Nội dung file `backend/.env`:
```env
PORT=5000
NODE_ENV=development

# Cấu hình kết nối MySQL Database
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=
DB_NAME=stayhub_db

# Cấu hình JWT
JWT_SECRET=stayhub_super_secret_jwt_key_2026
JWT_EXPIRES_IN=7d
```

### Bước 3: Khởi tạo Cơ sở dữ liệu tự động từ file .env
Hệ thống cung cấp script đọc trực tiếp các biến `DB_HOST`, `DB_PORT`, `DB_USER`, `DB_PASSWORD`, `DB_NAME` từ file `.env` để tạo database và nạp dữ liệu mẫu một cách tự động:
```bash
npm run db:init
```
Nếu muốn chạy thủ công bằng MySQL CLI hoặc phpMyAdmin:
```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
```

### Bước 4: Khởi động Server
- Chạy môi trường phát triển (tự động reload khi sửa code bằng nodemon):
  ```bash
  npm run dev
  ```
- Hoặc chạy môi trường Production:
  ```bash
  npm start
  ```

Server sẽ lắng nghe tại: `http://localhost:5000`  
Endpoint kiểm tra sức khỏe hệ thống: `http://localhost:5000/api/health`

---

## 4. TÀI KHOẢN MẪU CÓ SẴN TRONG DATABASE

Mật khẩu mặc định cho tất cả tài khoản mẫu là: **123456**

| Vai trò (Role) | Họ và tên | Email | Mật khẩu |
| :--- | :--- | :--- | :--- |
| admin | Admin Quản Trị Viên | admin@stayhub.com | 123456 |
| user | Nguyễn Văn Khách | khachhang1@gmail.com | 123456 |
| user | Trần Thị Thu Thảo | khachhang2@gmail.com | 123456 |

---

## 5. DANH SÁCH API ENDPOINTS

### 5.1 AUTHENTICATION
| Method | Endpoint | Quyền hạn | Mô tả |
| :--- | :--- | :--- | :--- |
| POST | /api/auth/register | Public | Đăng ký tài khoản người dùng mới (mặc định quyền user) |
| POST | /api/auth/login | Public | Đăng nhập hệ thống, trả về JWT Token |
| GET | /api/auth/me | user / admin | Lấy thông tin tài khoản đang đăng nhập |

### 5.2 HOMESTAY
| Method | Endpoint | Quyền hạn | Mô tả |
| :--- | :--- | :--- | :--- |
| GET | /api/homestays | Public | Xem danh sách homestay. Hỗ trợ bộ lọc: city, minPrice, maxPrice, guests, amenities, page, limit |
| GET | /api/homestays/:id | Public | Xem chi tiết homestay kèm danh sách phòng |
| POST | /api/homestays | admin | Tạo homestay mới |
| PUT | /api/homestays/:id | admin | Cập nhật thông tin homestay (chủ sở hữu hoặc admin) |
| DELETE | /api/homestays/:id | admin | Xóa homestay |

### 5.3 ROOM
| Method | Endpoint | Quyền hạn | Mô tả |
| :--- | :--- | :--- | :--- |
| GET | /api/homestays/:id/rooms | Public | Lấy danh sách phòng thuộc một homestay |
| GET | /api/rooms/:id | Public | Xem thông tin chi tiết một phòng |
| GET | /api/rooms/:id/availability | Public | Kiểm tra phòng còn trống hay không (?checkIn=YYYY-MM-DD&checkOut=YYYY-MM-DD) |
| POST | /api/homestays/:id/rooms | admin | Thêm phòng mới vào homestay |
| PUT | /api/rooms/:id | admin | Cập nhật thông tin phòng |
| DELETE | /api/rooms/:id | admin | Xóa phòng |

### 5.4 BOOKING
| Method | Endpoint | Quyền hạn | Mô tả |
| :--- | :--- | :--- | :--- |
| POST | /api/bookings | user / admin | Đặt phòng (tự động tính tổng tiền, trạng thái ban đầu là PENDING) |
| GET | /api/bookings/my | user / admin | Xem lịch sử đơn đặt phòng của tôi |
| GET | /api/bookings/:id | auth | Xem chi tiết một đơn đặt phòng (chủ booking hoặc admin) |
| PATCH | /api/bookings/:id/cancel | auth | Hủy đơn đặt phòng (chỉ áp dụng khi PENDING/CONFIRMED và trước check_in >= 24h) |
| PATCH | /api/bookings/:id/status | admin | Chuyển trạng thái booking: CONFIRMED, REJECTED, COMPLETED |
| GET | /api/admin/bookings | admin | Quản lý toàn bộ danh sách đơn đặt phòng trong hệ thống |

### 5.5 REVIEW
| Method | Endpoint | Quyền hạn | Mô tả |
| :--- | :--- | :--- | :--- |
| POST | /api/reviews | user / admin | Đánh giá homestay (chỉ khi booking đã COMPLETED, mỗi booking được review 1 lần) |
| GET | /api/homestays/:id/reviews | Public | Xem danh sách đánh giá của homestay kèm điểm trung bình avg_rating |
| DELETE | /api/reviews/:id | admin | Xóa đánh giá không phù hợp và tự động tính lại điểm avg_rating |

---

## 6. CÁC THUẬT TOÁN VÀ LOGIC QUAN TRỌNG

### 6.1 Logic Kiểm Tra Trùng Lịch (Check Overlap)
Một phòng bị coi là đã có khách đặt nếu tồn tại booking có trạng thái thuộc danh sách ('PENDING', 'CONFIRMED') và giao thoa với khoảng thời gian mới:
existing.check_in < new.check_out VÀ existing.check_out > new.check_in

Câu truy vấn SQL chuẩn:
```sql
SELECT 1 FROM bookings 
WHERE room_id = ? 
  AND status IN ('PENDING', 'CONFIRMED')
  AND check_in < ? AND check_out > ?
LIMIT 1;
```
(Dấu nhỏ hơn `<` và lớn hơn `>` đảm bảo rằng khách trả phòng buổi trưa ngày D thì khách mới vẫn có thể nhận phòng vào buổi chiều cùng ngày D mà không bị tính là trùng lịch).

### 6.2 Xử lý Race Condition bằng Transaction và Khóa Dòng (Lock)
Khi có 2 người dùng cùng lúc bấm đặt 1 phòng trong cùng một khoảng ngày:
1. conn.beginTransaction()
2. Khóa dòng phòng: SELECT ... FROM rooms WHERE id = ? FOR UPDATE;
3. Kiểm tra trùng lịch có khóa dòng: SELECT ... FROM bookings WHERE ... FOR UPDATE;
4. Nếu phát hiện trùng lịch: conn.rollback() và trả về mã lỗi 409 Conflict.
5. Nếu phòng còn trống: INSERT INTO bookings ..., sau đó conn.commit().

### 6.3 Quy tắc Hủy phòng
Khách hàng chỉ được phép hủy phòng khi:
1. Người gửi request chính là chủ của đơn đặt phòng (b.user_id = req.user.id).
2. Trạng thái hiện tại của đơn là PENDING hoặc CONFIRMED.
3. Thời điểm hiện tại phải cách giờ nhận phòng ngày check_in (mặc định 14:00) ít nhất 24 giờ.

### 6.4 Đánh giá và Tự động Cập nhật Điểm Trung bình (avg_rating)
- Điều kiện đánh giá: Đơn đặt phòng phải ở trạng thái COMPLETED (đã hoàn thành kỳ nghỉ).
- Ràng buộc toàn vẹn: Bảng reviews có ràng buộc UNIQUE (booking_id) nên mỗi đơn chỉ được đánh giá duy nhất 1 lần.
- Sau khi thêm hoặc xóa đánh giá, hệ thống tự động cập nhật lại điểm trung bình cho Homestay trong cùng Transaction:
```sql
UPDATE homestays 
SET avg_rating = COALESCE(
  (SELECT ROUND(AVG(rating), 1) FROM reviews WHERE homestay_id = ?),
  0.0
)
WHERE id = ?;
```

---

## 7. HƯỚNG DẪN KIỂM THỬ API (POSTMAN / CURL)

Có thể kiểm thử nhanh các API thông qua Postman hoặc lệnh cURL trên Terminal:

### 7.1 Kiểm tra trạng thái hệ thống (Health Check)
```bash
curl -X GET http://localhost:5000/api/health
```

### 7.2 Đăng nhập lấy Token
```bash
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"khachhang1@gmail.com","password":"password123"}'
```

### 7.3 Xem danh sách Homestay kèm bộ lọc
```bash
curl -X GET "http://localhost:5000/api/homestays?city=Da%20Lat&minPrice=400000&maxPrice=1000000"
```

### 7.4 Đặt phòng (Yêu cầu Bearer Token)
```bash
curl -X POST http://localhost:5000/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_CUA_BAN>" \
  -d '{"room_id":1,"check_in":"2026-11-15","check_out":"2026-11-18","guests":2,"note":"Phong yen tinh"}'
```
