const path = require('path');
const fs = require('fs');
const dotenv = require('dotenv');

// Nap file cau hinh .env ben trong thu muc backend
const backendEnvPath = path.resolve(__dirname, '../../.env');
const cwdEnvPath = path.resolve(process.cwd(), '.env');

if (fs.existsSync(backendEnvPath)) {
  dotenv.config({ path: backendEnvPath });
} else if (fs.existsSync(cwdEnvPath)) {
  dotenv.config({ path: cwdEnvPath });
} else {
  dotenv.config();
}

const env = {
  NODE_ENV: process.env.NODE_ENV || 'development',
  PORT: parseInt(process.env.PORT, 10) || 5000,
  
  // Cau hinh Database MySQL lay truc tiep tu .env
  DB_HOST: process.env.DB_HOST || 'localhost',
  DB_PORT: parseInt(process.env.DB_PORT, 10) || 3306,
  DB_USER: process.env.DB_USER || 'root',
  DB_PASSWORD: process.env.DB_PASSWORD || '',
  DB_NAME: process.env.DB_NAME || 'stayhub_db',

  // Cau hinh JWT
  JWT_SECRET: process.env.JWT_SECRET || 'stayhub_default_secret_key_change_in_production',
  JWT_EXPIRES_IN: process.env.JWT_EXPIRES_IN || '7d'
};

module.exports = env;
