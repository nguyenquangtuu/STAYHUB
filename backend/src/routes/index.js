const express = require('express');
const router = express.Router();

const authRoutes = require('./auth.routes');
const homestayRoutes = require('./homestay.routes');
const roomRoutes = require('./room.routes');
const bookingRoutes = require('./booking.routes');
const reviewRoutes = require('./review.routes');

const BookingController = require('../controllers/booking.controller');
const { verifyJWT, requireRole } = require('../middlewares/auth.middleware');

// Tuyến đường kiểm tra sức khỏe hệ thống (Health Check)
router.get('/health', (req, res) => {
  res.status(200).json({
    success: true,
    message: 'STAYHUB API server đang hoạt động bình thường',
    timestamp: new Date().toISOString()
  });
});

// Gắn các module route
router.use('/auth', authRoutes);
router.use('/homestays', homestayRoutes);
router.use('/rooms', roomRoutes);
router.use('/bookings', bookingRoutes);
router.use('/reviews', reviewRoutes);

// Endpoint đặc biệt theo yêu cầu đề bài: GET /api/admin/bookings [admin]
router.get(
  '/admin/bookings',
  verifyJWT,
  requireRole('admin'),
  BookingController.getAll
);

module.exports = router;

