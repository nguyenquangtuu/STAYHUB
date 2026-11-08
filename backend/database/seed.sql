SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ==============================================================
-- STAYHUB DATABASE SEED DATA
-- Dữ liệu mẫu chuẩn cho đồ án:
-- 1 Admin, 2 User (mật khẩu mặc định: 123456)
-- 3 Homestay (Hà Nội, Đà Lạt, Đà Nẵng)
-- 6 Phòng nghỉ với đa dạng mức giá và tiện ích
-- Các booking mẫu (COMPLETED, CONFIRMED) và review mẫu
-- ==============================================================

USE `stayhub_db`;

-- Xóa dữ liệu cũ theo thứ tự quan hệ
DELETE FROM `reviews`;
DELETE FROM `bookings`;
DELETE FROM `rooms`;
DELETE FROM `homestays`;
DELETE FROM `users`;

-- Reset AUTO_INCREMENT
ALTER TABLE `reviews` AUTO_INCREMENT = 1;
ALTER TABLE `bookings` AUTO_INCREMENT = 1;
ALTER TABLE `rooms` AUTO_INCREMENT = 1;
ALTER TABLE `homestays` AUTO_INCREMENT = 1;
ALTER TABLE `users` AUTO_INCREMENT = 1;

-- 1. TẠO USERS (Mật khẩu hash của '123456': $2a$10$N8mfOR8lphPfcXVbz2W4LehSkRKcdEVpNlJZbk4x2WRMhMKiHYzEK)
INSERT INTO `users` (`id`, `full_name`, `email`, `password_hash`, `phone`, `role`) VALUES
(1, 'Admin Quản Trị Viên', 'admin@stayhub.com', '$2a$10$N8mfOR8lphPfcXVbz2W4LehSkRKcdEVpNlJZbk4x2WRMhMKiHYzEK', '0901234567', 'admin'),
(2, 'Nguyễn Văn Khách', 'khachhang1@gmail.com', '$2a$10$N8mfOR8lphPfcXVbz2W4LehSkRKcdEVpNlJZbk4x2WRMhMKiHYzEK', '0912345678', 'user'),
(3, 'Trần Thị Thu Thảo', 'khachhang2@gmail.com', '$2a$10$N8mfOR8lphPfcXVbz2W4LehSkRKcdEVpNlJZbk4x2WRMhMKiHYzEK', '0923456789', 'user');

-- 2. TẠO 3 HOMESTAYS
INSERT INTO `homestays` (`id`, `owner_id`, `name`, `description`, `city`, `address`, `amenities`, `avg_rating`) VALUES
(1, 1, 'An Nam Heritage Villa', 'Biệt thự phong cách cổ điển truyền thống nằm ngay lòng phố cổ, không gian yên bình và ấm cúng.', 'Hà Nội', '12 Phố Chả Cá, Hoàn Kiếm, Hà Nội', '["wifi", "parking", "kitchen", "air_conditioning"]', 5.0),
(2, 1, 'Mây Lang Thang Homestay & Coffee', 'Homestay săn mây view thung lũng đồi thông cực chill, có quán cafe acoustic và tiệc nướng BBQ ngoài trời.', 'Đà Lạt', '77 Hoàng Hoa Thám, Phường 10, TP. Đà Lạt', '["wifi", "mountain_view", "bbq", "garden", "parking"]', 0.0),
(3, 1, 'Sơn Trà Ocean Retreat', 'Căn hộ nghỉ dưỡng cao cấp sát bờ biển Mỹ Khê, hồ bơi vô cực nhìn thẳng ra bán đảo Sơn Trà.', 'Đà Nẵng', '88 Võ Nguyên Giáp, Phước Mỹ, Sơn Trà, Đà Nẵng', '["wifi", "pool", "beach_front", "parking", "balcony"]', 0.0);

-- 3. TẠO 6 ROOMS (2 phòng cho mỗi homestay)
INSERT INTO `rooms` (`id`, `homestay_id`, `name`, `price_per_night`, `capacity`, `bed_type`, `status`) VALUES
-- Homestay 1: Hà Nội
(1, 1, 'Phòng Deluxe Phố Cổ', 650000.00, 2, 'King Bed', 'available'),
(2, 1, 'Phòng Gia Đình Hoàng Gia', 1200000.00, 4, '2 Queen Beds', 'available'),

-- Homestay 2: Đà Lạt
(3, 2, 'Phòng Áp Mái View Đồi Mộng Mơ', 500000.00, 2, 'Queen Bed', 'available'),
(4, 2, 'Bungalow Gỗ Thông Thung Lũng', 850000.00, 3, '1 King + 1 Single', 'available'),

-- Homestay 3: Đà Nẵng
(5, 3, 'Suite Hướng Biển Mỹ Khê', 1500000.00, 2, 'King Bed', 'available'),
(6, 3, 'Penthouse Hồ Bơi Riêng Cao Cấp', 2800000.00, 6, '3 Queen Beds', 'available');

-- 4. TẠO BOOKINGS MẪU
-- Booking 1: Đã hoàn thành (COMPLETED) của user 2 ở phòng 1 -> dùng để test Review
INSERT INTO `bookings` (`id`, `user_id`, `room_id`, `check_in`, `check_out`, `guests`, `total_price`, `status`, `note`) VALUES
(1, 2, 1, '2026-02-01', '2026-02-03', 2, 1300000.00, 'COMPLETED', 'Yêu cầu phòng tầng cao, check in 14h');

-- Booking 2: Đang được xác nhận (CONFIRMED) của user 3 ở phòng 3 -> dùng để test trùng lịch
INSERT INTO `bookings` (`id`, `user_id`, `room_id`, `check_in`, `check_out`, `guests`, `total_price`, `status`, `note`) VALUES
(2, 3, 3, '2026-12-20', '2026-12-25', 2, 2500000.00, 'CONFIRMED', 'Đi du lịch nghỉ dưỡng Đà Lạt dịp Giáng sinh');

-- 5. TẠO REVIEW MẪU CHO BOOKING ĐÃ HOÀN THÀNH
INSERT INTO `reviews` (`id`, `booking_id`, `user_id`, `homestay_id`, `rating`, `comment`) VALUES
(1, 1, 2, 1, 5, 'Phòng ốc sạch sẽ, vị trí ngay phố cổ rất thuận tiện đi dạo hồ Gươm. Chủ nhà hỗ trợ cực kỳ nhiệt tình!');

