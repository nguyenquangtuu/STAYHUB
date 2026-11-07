const express = require('express');
const router = express.Router();
const ReviewController = require('../controllers/review.controller');
const { createReviewValidator } = require('../validators/review.validator');
const { validateRequest } = require('../middlewares/validate.middleware');
const { verifyJWT, requireRole } = require('../middlewares/auth.middleware');

// POST /api/reviews - Gửi đánh giá cho booking đã hoàn thành [auth]
router.post(
  '/',
  verifyJWT,
  createReviewValidator,
  validateRequest,
  ReviewController.create
);

// DELETE /api/reviews/:id - Xóa đánh giá [admin]
router.delete(
  '/:id',
  verifyJWT,
  requireRole('admin'),
  ReviewController.delete
);

module.exports = router;
