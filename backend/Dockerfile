# Sử dụng Node.js LTS bản Alpine nhẹ và bảo mật
FROM node:18-alpine

WORKDIR /app

# Cài đặt dependencies trước để tối ưu layer cache của Docker
COPY package*.json ./

RUN npm install --omit=dev

# Sao chép toàn bộ mã nguồn backend vào container
COPY . .

# Expose port 5000 cho RESTful API
EXPOSE 5000

# Khởi động Backend server
CMD ["npm", "start"]
