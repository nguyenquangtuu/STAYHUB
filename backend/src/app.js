const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const routes = require('./routes');
const { errorHandler, notFoundHandler } = require('./middlewares/error.middleware');
const env = require('./config/env');

const app = express();

// 1. Bảo mật HTTP headers
app.use(helmet());

// 2. Cấu hình CORS
app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

// 3. Request Logger
if (env.NODE_ENV !== 'test') {
  app.use(morgan(env.NODE_ENV === 'development' ? 'dev' : 'combined'));
}

// 4. Parser Body dữ liệu
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// 5. Tuyến đường API chính
app.use('/api', routes);

// 6. Xử lý Route 404 (Không tìm thấy endpoint)
app.use(notFoundHandler);

// 7. Global Error Handler (Bắt toàn bộ lỗi phát sinh)
app.use(errorHandler);

module.exports = app;
