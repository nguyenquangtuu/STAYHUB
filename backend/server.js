const app = require('./src/app');
const env = require('./src/config/env');
const { testConnection, pool } = require('./src/config/database');

async function startServer() {
  try {
    // 1. Kiem tra ket noi toi co so du lieu MySQL
    console.log('[Server] Đang kết nối tới MySQL Database...');
    await testConnection();

    // 2. Khoi dong lang nghe cong
    const server = app.listen(env.PORT, () => {
      console.log('====================================================');
      console.log(`[Server] STAYHUB Backend API Server đang chạy`);
      console.log(`[Server] URL: http://localhost:${env.PORT}`);
      console.log(`[Server] Môi trường: ${env.NODE_ENV}`);
      console.log(`[Server] Health check: http://localhost:${env.PORT}/api/health`);
      console.log('====================================================');
    });

    // 3. Khởi động worker tự động xác nhận đơn PENDING sau 30 giây
    const BookingService = require('./src/services/booking.service');
    const autoConfirmInterval = setInterval(() => {
      BookingService.processPendingAutoConfirm();
    }, 10 * 1000);

    // 4. Xu ly dong ket noi an toan (Graceful Shutdown)
    const gracefulShutdown = async (signal) => {
      console.log(`\n[Server] Nhận tín hiệu ${signal}. Đang đóng các kết nối...`);
      clearInterval(autoConfirmInterval);
      server.close(async () => {
        console.log('[Server] Đã đóng HTTP server.');
        try {
          await pool.end();
          console.log('[Database] Đã đóng MySQL connection pool.');
          process.exit(0);
        } catch (err) {
          console.error('[Database] Lỗi khi đóng connection pool:', err);
          process.exit(1);
        }
      });
    };

    process.on('SIGINT', () => gracefulShutdown('SIGINT'));
    process.on('SIGTERM', () => gracefulShutdown('SIGTERM'));

  } catch (error) {
    console.error('[Server] Khởi động server thất bại:', error.message);
    process.exit(1);
  }
}

startServer();
