const fs = require('fs');
const path = require('path');
const mysql = require('mysql2/promise');
const env = require('../src/config/env');

/**
 * Script tu dong khoi tao database tu file .env
 * Doc truc tiep cac bien: DB_HOST, DB_PORT, DB_USER, DB_PASSWORD, DB_NAME tu .env
 */
async function initializeDatabase() {
  console.log('[Database Init] Dang ket noi MySQL server de khoi tao database tu file .env...');
  console.log(`[Database Init] Host: ${env.DB_HOST}:${env.DB_PORT} | User: ${env.DB_USER} | Target Database: ${env.DB_NAME}`);

  let connection;
  try {
    // 1. Ket noi den MySQL server (chua chon database cu the)
    connection = await mysql.createConnection({
      host: env.DB_HOST,
      port: env.DB_PORT,
      user: env.DB_USER,
      password: env.DB_PASSWORD,
      multipleStatements: true
    });

    // 2. Tao Database theo ten cau hinh trong .env neu chua co
    await connection.query(
      `CREATE DATABASE IF NOT EXISTS \`${env.DB_NAME}\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
    );
    console.log(`[Database Init] Database "${env.DB_NAME}" da san sang.`);

    // 3. Chuyen vao su dung database duoc chi dinh trong .env
    await connection.query(`USE \`${env.DB_NAME}\`;`);

    // 4. Doc va thuc thi schema.sql (thay the ten database thanh DB_NAME tu .env neu co)
    const schemaPath = path.resolve(__dirname, 'schema.sql');
    if (fs.existsSync(schemaPath)) {
      let schemaSql = fs.readFileSync(schemaPath, 'utf8');
      schemaSql = schemaSql.replace(/`stayhub_db`/g, `\`${env.DB_NAME}\``);
      await connection.query(schemaSql);
      console.log('[Database Init] Da thuc thi schema.sql thanh cong.');
    }

    // 5. Kiem tra xem co chay seed.sql khong (mac dinh hoac khi co flag --seed)
    const shouldSeed = process.argv.includes('--seed') || !process.argv.includes('--no-seed');
    if (shouldSeed) {
      const seedPath = path.resolve(__dirname, 'seed.sql');
      if (fs.existsSync(seedPath)) {
        let seedSql = fs.readFileSync(seedPath, 'utf8');
        seedSql = seedSql.replace(/`stayhub_db`/g, `\`${env.DB_NAME}\``);
        await connection.query(seedSql);
        console.log('[Database Init] Da thuc thi seed.sql thanh cong.');
      }
    }

    console.log('[Database Init] Khoi tao co so du lieu hoan tat.');
  } catch (error) {
    console.error('[Database Init] Loi khi khoi tao database:', error.message);
    process.exit(1);
  } finally {
    if (connection) {
      await connection.end();
    }
  }
}

initializeDatabase();
