const express = require('express');
const router = express.Router();
const RoomController = require('../controllers/room.controller');
const { updateRoomValidator } = require('../validators/room.validator');
const { validateRequest } = require('../middlewares/validate.middleware');
const { verifyJWT, requireRole } = require('../middlewares/auth.middleware');

// GET /api/rooms/:id/availability?checkIn=YYYY-MM-DD&checkOut=YYYY-MM-DD - Kiểm tra phòng trống
router.get('/:id/availability', RoomController.checkAvailability);

// GET /api/rooms/:id - Chi tiết một phòng
router.get('/:id', RoomController.getById);

// PUT /api/rooms/:id - Cập nhật phòng [admin]
router.put(
  '/:id',
  verifyJWT,
  requireRole('admin'),
  updateRoomValidator,
  validateRequest,
  RoomController.update
);

// DELETE /api/rooms/:id - Xóa phòng [admin]
router.delete(
  '/:id',
  verifyJWT,
  requireRole('admin'),
  RoomController.delete
);

module.exports = router;
