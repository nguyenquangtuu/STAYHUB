const app = require('./src/app');
const env = require('./src/config/env');
const { testConnection, pool } = require('./src/config/database');

async function startServer() {
  try {
    // 1. Kiểm tra kết nối tới cơ sở dữ liệu MySQL
    console.log('[Server] Đang kết nối tới MySQL Database...');
    await testConnection();

    // 2. Khởi động lắng nghe cổng
    const server = app.listen(env.PORT, () => {
      console.log('====================================================');
      console.log(`🚀 STAYHUB Backend API Server đang chạy`);
      console.log(`📡 URL: http://localhost:${env.PORT}`);
      console.log(`🌿 Môi trường: ${env.NODE_ENV}`);
      console.log(`🩺 Health check: http://localhost:${env.PORT}/api/health`);
      console.log('====================================================');
    });

    // 3. Xử lý đóng kết nối an toàn (Graceful Shutdown)
    const gracefulShutdown = async (signal) => {
      console.log(`\n[Server] Nhận tín hiệu ${signal}. Đang đóng các kết nối...`);
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
    console.error('❌ Khởi động server thất bại:', error.message);
    process.exit(1);
  }
}

startServer();
