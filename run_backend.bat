@echo off
chcp 65001 >nul
title [StayHub] Backend Server API

echo ======================================================================
echo             STAYHUB - KHỞI ĐỘNG BACKEND RESTFUL API
echo ======================================================================
echo.
echo  [1] Khoi dong bang Docker (Khuyen nghi: tu dong tao MySQL 8 + Seed data)
echo  [2] Khoi dong bang NodeJS & MySQL Local (XAMPP / MySQL may thuc)
echo.
set /p opt="Chon phuong thuc khoi dong (1 hoac 2) [Mac dinh: 1]: "

if "%opt%"=="2" goto run_node

:run_docker
call "%~dp0run_backend_docker.bat"
exit /b 0

:run_node
cd /d "%~dp0backend"

if not exist "node_modules" (
    echo [..] Dang cai dat dependencies...
    call npm install
)

if not exist ".env" (
    echo [..] Tao file .env tu .env.example...
    copy .env.example .env
)

echo [OK] Dang khoi dong Backend API tai port 5000...
npm run dev
pause
