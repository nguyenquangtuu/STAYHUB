const express = require('express');
const router = express.Router();
const AuthController = require('../controllers/auth.controller');
const { 
  sendOtpValidator, 
  verifyOtpValidator, 
  completeRegistrationValidator,
  updateProfileValidator,
  registerValidator, 
  loginValidator 
} = require('../validators/auth.validator');
const { validateRequest } = require('../middlewares/validate.middleware');
const { verifyJWT } = require('../middlewares/auth.middleware');

// POST /api/auth/send-otp
router.post('/send-otp', sendOtpValidator, validateRequest, AuthController.sendOtp);

// POST /api/auth/verify-otp
router.post('/verify-otp', verifyOtpValidator, validateRequest, AuthController.verifyOtp);

// POST /api/auth/complete-registration
router.post('/complete-registration', completeRegistrationValidator, validateRequest, AuthController.completeRegistration);

// POST /api/auth/register (truyền thống)
router.post('/register', registerValidator, validateRequest, AuthController.register);

// POST /api/auth/login (truyền thống)
router.post('/login', loginValidator, validateRequest, AuthController.login);

// GET /api/auth/me [auth]
router.get('/me', verifyJWT, AuthController.getMe);

// PUT /api/auth/me hoặc /api/auth/profile [auth]
router.put('/me', verifyJWT, updateProfileValidator, validateRequest, AuthController.updateProfile);
router.put('/profile', verifyJWT, updateProfileValidator, validateRequest, AuthController.updateProfile);

module.exports = router;

