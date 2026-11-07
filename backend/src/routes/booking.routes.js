const express = require('express');
const router = express.Router();
const BookingController = require('../controllers/booking.controller');
const { createBookingValidator, updateBookingStatusValidator } = require('../validators/booking.validator');
const { validateRequest } = require('../middlewares/validate.middleware');
const { verifyJWT, requireRole } = require('../middlewares/auth.middleware');

// POST /api/bookings - Tạo booking mới [auth]
router.post(
  '/',
  verifyJWT,
  createBookingValidator,
  validateRequest,
  BookingController.create
);

// GET /api/bookings/my - Danh sách booking của tôi [auth]
router.get('/my', verifyJWT, BookingController.getMyBookings);

// PATCH /api/bookings/:id/cancel - Hủy booking [auth - chủ booking]
router.patch('/:id/cancel', verifyJWT, BookingController.cancel);

// PATCH /api/bookings/:id/status - Admin confirm/reject/complete [admin]
router.patch(
  '/:id/status',
  verifyJWT,
  requireRole('admin'),
  updateBookingStatusValidator,
  validateRequest,
  BookingController.updateStatus
);

// GET /api/bookings/:id - Chi tiết một booking [auth]
router.get('/:id', verifyJWT, BookingController.getById);

module.exports = router;

