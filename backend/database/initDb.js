const fs = require('fs');
const path = require('path');
const mysql = require('mysql2/promise');
const env = require('../src/config/env');

/**
 * Script tự động khởi tạo database từ file .env
 * Đọc trực tiếp các biến: DB_HOST, DB_PORT, DB_USER, DB_PASSWORD, DB_NAME từ .env
 */
async function initializeDatabase() {
  console.log('[Database Init] Đang kết nối MySQL server để khởi tạo database từ file .env...');
  console.log(`[Database Init] Host: ${env.DB_HOST}:${env.DB_PORT} | User: ${env.DB_USER} | Target Database: ${env.DB_NAME}`);

  let connection;
  try {
    // 1. Kết nối đến MySQL server (chưa chọn database cụ thể)
    connection = await mysql.createConnection({
      host: env.DB_HOST,
      port: env.DB_PORT,
      user: env.DB_USER,
      password: env.DB_PASSWORD,
      multipleStatements: true
    });

    // 2. Tạo Database theo tên cấu hình trong .env nếu chưa có
    await connection.query(
      `CREATE DATABASE IF NOT EXISTS \`${env.DB_NAME}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
    );
    console.log(`[Database Init] Database "${env.DB_NAME}" đã sẵn sàng.`);

    // 3. Chuyển vào sử dụng database được chỉ định trong .env
    await connection.query(`USE \`${env.DB_NAME}\`;`);

    // 4. Đọc và thực thi schema.sql (thay thế tên database thành DB_NAME từ .env nếu có)
    const schemaPath = path.resolve(__dirname, 'schema.sql');
    if (fs.existsSync(schemaPath)) {
      let schemaSql = fs.readFileSync(schemaPath, 'utf8');
      schemaSql = schemaSql.replace(/`stayhub_db`/g, `\`${env.DB_NAME}\``);
      await connection.query(schemaSql);
      console.log('[Database Init] Đã thực thi schema.sql thành công.');
    }

    // 5. Kiểm tra xem có chạy seed.sql không (mặc định hoặc khi có flag --seed)
    const shouldSeed = process.argv.includes('--seed') || !process.argv.includes('--no-seed');
    if (shouldSeed) {
      const seedPath = path.resolve(__dirname, 'seed.sql');
      if (fs.existsSync(seedPath)) {
        let seedSql = fs.readFileSync(seedPath, 'utf8');
        seedSql = seedSql.replace(/`stayhub_db`/g, `\`${env.DB_NAME}\``);
        await connection.query(seedSql);
        console.log('[Database Init] Đã thực thi seed.sql thành công.');
      }
    }

    console.log('[Database Init] Khởi tạo cơ sở dữ liệu hoàn tất.');
  } catch (error) {
    console.error('[Database Init] Lỗi khi khởi tạo database:', error.message);
    process.exit(1);
  } finally {
    if (connection) {
      await connection.end();
    }
  }
}

initializeDatabase();
