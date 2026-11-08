const express = require('express');
const router = express.Router();
const AuthController = require('../controllers/auth.controller');
const { registerValidator, loginValidator } = require('../validators/auth.validator');
const { validateRequest } = require('../middlewares/validate.middleware');
const { verifyJWT } = require('../middlewares/auth.middleware');

// POST /api/auth/register
router.post('/register', registerValidator, validateRequest, AuthController.register);

// POST /api/auth/login
router.post('/login', loginValidator, validateRequest, AuthController.login);

// GET /api/auth/me [auth]
router.get('/me', verifyJWT, AuthController.getMe);

// PUT /api/auth/me hoặc /api/auth/profile [auth]
router.put('/me', verifyJWT, AuthController.updateProfile);
router.put('/profile', verifyJWT, AuthController.updateProfile);

module.exports = router;

