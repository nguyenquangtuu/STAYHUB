# STAYHUB - BACKEND RESTFUL API

He thong Backend API cho ung dung dat phong Homestay nghi duong STAYHUB, duoc xay dung bang NodeJS + Express + MySQL theo mo hinh kien truc phan lop chuan cong nghiep (3-Tier Architecture), toi uu hoa hieu nang, xu ly dong thoi (Concurrency Control) va dam bao cac tieu chuan do an tot nghiep / do an chuyen nganh CNTT.

---

## 1. TECH STACK VA QUY UOC CONG NGHE

- Runtime va Ngon ngu: Node.js v18+ (CommonJS)
- Web Framework: Express.js
- He quan tri CSDL: MySQL 8.0+ / MariaDB
- Database Driver: mysql2/promise (Su dung Connection Pool, Raw SQL voi Prepared Statements de toi uu hieu nang va chong SQL Injection, khong dung ORM)
- Bao mat:
  - bcryptjs: Bam mat khau (10 salt rounds)
  - jsonwebtoken (JWT): Xac thuc phien lam viec Stateless
  - helmet: Tang cuong bao mat HTTP Headers
  - cors: Cau hinh chia se tai nguyen nguon goc cheo
- Kiem tra du lieu dau vao: express-validator
- Logging: morgan

---

## 2. CAU TRUC THU MUC DU AN

```
backend/
├── src/
│   ├── config/
│   │   ├── database.js          # Khoi tao MySQL Connection Pool va kiem tra ket noi
│   │   └── env.js               # Load va kiem soat bien moi truong tu file .env
│   ├── models/                  # Tang thao tac CSDL thuan (Raw SQL Prepared Statements)
│   │   ├── user.model.js
│   │   ├── homestay.model.js    # Tim kiem dong (city, price, capacity, amenities)
│   │   ├── room.model.js
│   │   ├── booking.model.js     # Query kiem tra trung lich va khoa dong FOR UPDATE
│   │   └── review.model.js      # Luu review va tinh lai avg_rating
│   ├── controllers/             # Tiep nhan request HTTP, goi service va tra response
│   │   ├── auth.controller.js
│   │   ├── homestay.controller.js
│   │   ├── room.controller.js
│   │   ├── booking.controller.js
│   │   └── review.controller.js
│   ├── services/                # Tang nghiep vu cot loi (Business Logic & Transactions)
│   │   ├── auth.service.js
│   │   ├── homestay.service.js
│   │   ├── room.service.js
│   │   ├── booking.service.js   # ACID Transaction dat phong, huy phong >= 24h
│   │   └── review.service.js    # Kiem tra booking COMPLETED va cap nhat avg_rating
│   ├── routes/                  # Dinh nghia endpoints va gan middlewares
│   │   ├── index.js             # Gom cum tat ca route ve tien to /api
│   │   ├── auth.routes.js
│   │   ├── homestay.routes.js
│   │   ├── room.routes.js
│   │   ├── booking.routes.js
│   │   └── review.routes.js
│   ├── middlewares/             # Cac bo loc trung gian
│   │   ├── auth.middleware.js   # verifyJWT, requireRole('user', 'admin')
│   │   ├── validate.middleware.js # Bat loi tu express-validator
│   │   └── error.middleware.js  # Global Error Handler chuan hoa loi
│   ├── validators/              # Dinh nghia quy tac validate body/param/query
│   │   ├── auth.validator.js
│   │   ├── homestay.validator.js
│   │   ├── room.validator.js
│   │   ├── booking.validator.js
│   │   └── review.validator.js
│   ├── utils/                   # Ham tien ich dung chung
│   │   ├── response.js          # Format JSON response thong nhat
│   │   ├── ApiError.js          # Custom Error Class chuan HTTP status
│   │   ├── dateHelper.js        # Tinh so dem, kiem tra dieu kien huy 24h
│   │   └── pagination.js        # Helper phan trang chuan
│   └── app.js                   # Cau hinh Express App
├── database/
│   ├── initDb.js                # Script tu dong doc cau hinh tu .env de tao DB va import SQL
│   ├── schema.sql               # Kich ban tao Database, Tables 3NF, Foreign Keys & Indexes
│   └── seed.sql                 # Du lieu mau (1 Admin, 2 User, 3 Homestay, 6 Room)
├── test.http                    # Bo kich ban kiem thu API day du cho REST Client
├── .env.example                 # Mau cau hinh bien moi truong
├── .gitignore
├── package.json
├── server.js                    # File khoi dong Server
└── README.md
```

---

## 3. HUONG DAN CAI DAT VA KHOI CHAY

### Buoc 1: Cai dat Dependencies
Di chuyen vao thu muc backend va chay lenh:
```bash
cd backend
npm install
```

### Buoc 2: Cau hinh bien moi truong (.env)
Toan bo thong tin ket noi CSDL duoc dat trong file `.env` nam ben trong thu muc `backend/`.
Tao file `backend/.env` tu `backend/.env.example`:
```bash
cp .env.example .env
```
Noi dung file `backend/.env`:
```env
PORT=5000
NODE_ENV=development

# Cau hinh ket noi MySQL Database
DB_HOST=localhost
DB_PORT=3306
DB_USER=root
DB_PASSWORD=
DB_NAME=stayhub_db

# Cau hinh JWT
JWT_SECRET=stayhub_super_secret_jwt_key_2026
JWT_EXPIRES_IN=7d
```

### Buoc 3: Khoi tao Co so du lieu tu file .env
He thong ho tro script tu dong doc truc tiep cac bien `DB_HOST`, `DB_PORT`, `DB_USER`, `DB_PASSWORD`, `DB_NAME` tu file `.env` de tao database va nap du lieu mau:
```bash
npm run db:init
```
Neu muon chay thu cong qua MySQL CLI hoac phpMyAdmin:
```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
```

### Buoc 4: Khoi dong Server
- Chay moi truong phat trien (tu reload khi sua code voi nodemon):
  ```bash
  npm run dev
  ```
- Hoac chay moi truong Production:
  ```bash
  npm start
  ```

Server se lang nghe tai: `http://localhost:5000`  
Endpoint kiem tra suc khoe: `http://localhost:5000/api/health`

---

## 4. TAI KHOAN MAU CO SAN TRONG DATABASE

Mat khau mac dinh cho tat ca tai khoan mau la: **123456**

| Vai tro (Role) | Ho va ten | Email | Mat khau |
| :--- | :--- | :--- | :--- |
| admin | Admin Quan Tri Vien | admin@stayhub.com | 123456 |
| user | Nguyen Van Khach | khachhang1@gmail.com | 123456 |
| user | Tran Thi Thu Thao | khachhang2@gmail.com | 123456 |

---

## 5. DANH SACH API ENDPOINTS

### 5.1 AUTHENTICATION
| Method | Endpoint | Quyen | Mo ta |
| :--- | :--- | :--- | :--- |
| POST | /api/auth/register | Public | Dang ky tai khoan moi (user hoac admin) |
| POST | /api/auth/login | Public | Dang nhap he thong, tra ve JWT Token |
| GET | /api/auth/me | user / admin | Lay thong tin tai khoan hien tai |

### 5.2 HOMESTAY
| Method | Endpoint | Quyen | Mo ta |
| :--- | :--- | :--- | :--- |
| GET | /api/homestays | Public | Xem danh sach homestay. Ho tro query params: city, minPrice, maxPrice, guests, amenities, page, limit |
| GET | /api/homestays/:id | Public | Chi tiet homestay kem danh sach phong |
| POST | /api/homestays | admin | Tao homestay moi |
| PUT | /api/homestays/:id | admin | Cap nhat thong tin homestay (chu so huu hoac admin) |
| DELETE | /api/homestays/:id | admin | Xoa homestay |

### 5.3 ROOM
| Method | Endpoint | Quyen | Mo ta |
| :--- | :--- | :--- | :--- |
| GET | /api/homestays/:id/rooms | Public | Lay danh sach phong thuoc mot homestay |
| GET | /api/rooms/:id | Public | Xem thong tin chi tiet mot phong |
| GET | /api/rooms/:id/availability | Public | Kiem tra phong trong (?checkIn=YYYY-MM-DD&checkOut=YYYY-MM-DD) |
| POST | /api/homestays/:id/rooms | admin | Them phong moi vao homestay |
| PUT | /api/rooms/:id | admin | Cap nhat thong tin phong |
| DELETE | /api/rooms/:id | admin | Xoa phong |

### 5.4 BOOKING
| Method | Endpoint | Quyen | Mo ta |
| :--- | :--- | :--- | :--- |
| POST | /api/bookings | user / admin | Dat phong (tu dong tinh tong tien, trang thai ban dau PENDING) |
| GET | /api/bookings/my | user / admin | Xem lich su don dat phong cua toi |
| GET | /api/bookings/:id | auth | Chi tiet mot don dat phong (chu booking hoac admin) |
| PATCH | /api/bookings/:id/cancel | auth | Huy don dat phong (chi khi PENDING/CONFIRMED va truoc check_in >= 24h) |
| PATCH | /api/bookings/:id/status | admin | Chuyen trang thai booking: CONFIRMED, REJECTED, COMPLETED |
| GET | /api/admin/bookings | admin | Quan ly toan bo danh sach booking trong he thong |

### 5.5 REVIEW
| Method | Endpoint | Quyen | Mo ta |
| :--- | :--- | :--- | :--- |
| POST | /api/reviews | user / admin | Danh gia homestay (chi khi booking da COMPLETED, moi booking review 1 lan) |
| GET | /api/homestays/:id/reviews | Public | Xem danh sach danh gia cua homestay kem diem avg_rating |
| DELETE | /api/reviews/:id | admin | Xoa danh gia khong phu hop va tu dong tinh lai avg_rating |

---

## 6. CAC THUAT TOAN VA LOGIC QUAN TRONG

### 6.1 Logic Kiem Tra Trung Lich (Check Overlap)
Mot phong bi coi la da co khach dat neu ton tai booking co trang thai IN ('PENDING', 'CONFIRMED') va giao voi khoang thoi gian moi:
existing.check_in < new.check_out AND existing.check_out > new.check_in

Cau truy van SQL chuan:
```sql
SELECT 1 FROM bookings 
WHERE room_id = ? 
  AND status IN ('PENDING', 'CONFIRMED')
  AND check_in < ? AND check_out > ?
LIMIT 1;
```
(Dau < va > dam bao rang khach tra phong buoi trua ngay D thi khach moi co the nhan phong buoi chieu cung ngay D ma khong bi tinh la trung lich).

### 6.2 Xu ly Race Condition bang Transaction va Lock
Khi co 2 nguoi dung cung luc dat 1 phong trong cung mot khoang ngay:
1. conn.beginTransaction()
2. Khoa dong phong: SELECT ... FROM rooms WHERE id = ? FOR UPDATE;
3. Kiem tra trung lich co khoa dong: SELECT ... FROM bookings WHERE ... FOR UPDATE;
4. Neu phat hien trung: conn.rollback() va tra ve ma loi 409 Conflict.
5. Neu hop le: INSERT INTO bookings ..., sau do conn.commit().

### 6.3 Quy tac Huy phong
Khach hang chi duoc phep huy phong khi:
1. Nguoi gui request chinh la chu cua don dat phong (b.user_id = req.user.id).
2. Trang thai hien tai cua don la PENDING hoac CONFIRMED.
3. Thoi diem hien tai phai cach gio nhan phong ngay check_in (mac dinh 14:00) it nhat 24 gio.

### 6.4 Danh gia va Tu dong Cap nhat Diem Trung binh (avg_rating)
- Dieu kien danh gia: Don dat phong phai o trang thai COMPLETED (da hoan thanh chuyen di).
- Rang buoc toan ven: Bang reviews co rang buoc UNIQUE (booking_id) nen moi don chi duoc danh gia duy nhat 1 lan.
- Sau khi them hoac xoa danh gia, he thong tu dong cap nhat lai diem trung binh cho Homestay trong cung Transaction:
```sql
UPDATE homestays 
SET avg_rating = COALESCE(
  (SELECT ROUND(AVG(rating), 1) FROM reviews WHERE homestay_id = ?),
  0.0
)
WHERE id = ?;
```

---

## 7. HUONG DAN TEST API BANG FILE test.http

Du an da tich hop san bo kiem thu toan dien trong file backend/test.http.
Cach su dung:
1. Cai dat tien ich mo rong REST Client tren VS Code (cua Huachao Mao).
2. Mo file backend/test.http.
3. Nhan chuot vao nut "Send Request" nam ngay phia tren tung dong request de thuc thi:
   - Chay request 2.2 Dang nhap User de token tu dong duoc luu vao bien @userToken.
   - Chay request 2.3 Dang nhap Admin de token tu dong duoc luu vao bien @adminToken.
   - Thuc hien test dat phong, kiem tra trung lich (nhan 409 Conflict), test huy phong va gui danh gia.
