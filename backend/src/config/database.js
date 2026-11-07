const mysql = require('mysql2/promise');
const env = require('./env');

// Khởi tạo Connection Pool cho MySQL
const pool = mysql.createPool({
  host: env.DB_HOST,
  port: env.DB_PORT,
  user: env.DB_USER,
  password: env.DB_PASSWORD,
  database: env.DB_NAME,
  waitForConnections: true,
  connectionLimit: 15,
  queueLimit: 0,
  charset: 'utf8mb4'
});

/**
 * Kiểm tra kết nối tới MySQL database khi server khởi động
 */
async function testConnection() {
  try {
    const connection = await pool.getConnection();
    console.log(`[Database] Kết nối MySQL thành công tới database: ${env.DB_NAME}`);
    connection.release();
    return true;
  } catch (error) {
    console.error('[Database] Lỗi kết nối MySQL:', error.message);
    throw error;
  }
}

module.exports = {
  pool,
  testConnection
};

