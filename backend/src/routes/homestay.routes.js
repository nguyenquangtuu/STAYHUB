const express = require('express');
const router = express.Router();
const HomestayController = require('../controllers/homestay.controller');
const RoomController = require('../controllers/room.controller');
const ReviewController = require('../controllers/review.controller');
const { createHomestayValidator, updateHomestayValidator } = require('../validators/homestay.validator');
const { createRoomValidator } = require('../validators/room.validator');
const { validateRequest } = require('../middlewares/validate.middleware');
const { verifyJWT, requireRole } = require('../middlewares/auth.middleware');

// GET /api/homestays - Danh sách homestay kèm bộ lọc & phân trang
router.get('/', HomestayController.getAll);

// GET /api/homestays/:id - Chi tiết homestay
router.get('/:id', HomestayController.getById);

// POST /api/homestays - Tạo homestay mới [admin]
router.post(
  '/',
  verifyJWT,
  requireRole('admin'),
  createHomestayValidator,
  validateRequest,
  HomestayController.create
);

// PUT /api/homestays/:id - Cập nhật homestay [admin - chủ sở hữu]
router.put(
  '/:id',
  verifyJWT,
  requireRole('admin'),
  updateHomestayValidator,
  validateRequest,
  HomestayController.update
);

// DELETE /api/homestays/:id - Xóa homestay [admin]
router.delete(
  '/:id',
  verifyJWT,
  requireRole('admin'),
  HomestayController.delete
);

// --- Sub-routes liên kết ---

// GET /api/homestays/:id/rooms - Lấy danh sách phòng của homestay
router.get('/:id/rooms', RoomController.getByHomestay);

// POST /api/homestays/:id/rooms - Tạo phòng mới cho homestay [admin]
router.post(
  '/:id/rooms',
  verifyJWT,
  requireRole('admin'),
  createRoomValidator,
  validateRequest,
  RoomController.create
);

// GET /api/homestays/:id/reviews - Lấy danh sách đánh giá của homestay
router.get('/:id/reviews', ReviewController.getByHomestay);

module.exports = router;

