@echo off
chcp 65001 >nul
title [StayHub] Backend Server API

echo ======================================================================
echo             STAYHUB - KHỞI ĐỘNG BACKEND RESTFUL API
echo ======================================================================
echo.

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
