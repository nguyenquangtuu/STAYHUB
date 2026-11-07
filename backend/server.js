const app = require('./src/app');
const env = require('./src/config/env');
const { testConnection, pool } = require('./src/config/database');

async function startServer() {
  try {
    // 1. Kiem tra ket noi toi co so du lieu MySQL
    console.log('[Server] Dang ket noi toi MySQL Database...');
    await testConnection();

    // 2. Khoi dong lang nghe cong
    const server = app.listen(env.PORT, () => {
      console.log('====================================================');
      console.log(`[Server] STAYHUB Backend API Server dang chay`);
      console.log(`[Server] URL: http://localhost:${env.PORT}`);
      console.log(`[Server] Environment: ${env.NODE_ENV}`);
      console.log(`[Server] Health check: http://localhost:${env.PORT}/api/health`);
      console.log('====================================================');
    });

    // 3. Xu ly dong ket noi an toan (Graceful Shutdown)
    const gracefulShutdown = async (signal) => {
      console.log(`\n[Server] Nhan tin hieu ${signal}. Dang dong cac ket noi...`);
      server.close(async () => {
        console.log('[Server] Da dong HTTP server.');
        try {
          await pool.end();
          console.log('[Database] Da dong MySQL connection pool.');
          process.exit(0);
        } catch (err) {
          console.error('[Database] Loi khi dong connection pool:', err);
          process.exit(1);
        }
      });
    };

    process.on('SIGINT', () => gracefulShutdown('SIGINT'));
    process.on('SIGTERM', () => gracefulShutdown('SIGTERM'));

  } catch (error) {
    console.error('[Server] Khoi dong server that bai:', error.message);
    process.exit(1);
  }
}

startServer();
