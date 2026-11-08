@echo off
title StayHub - Backend Server API

echo ======================================================================
echo             STAYHUB - KHOI DONG BACKEND RESTFUL API
echo ======================================================================
echo.
echo  [1] Khoi dong bang Docker (Tu dong MySQL 8 va Seed du lieu mau)
echo  [2] Khoi dong bang NodeJS va MySQL Local
echo.
set /p opt="Chon phuong thuc khoi dong (1 hoac 2) [Mac dinh: 1]: "

if "%opt%"=="2" goto run_node

:run_docker
call "%~dp0run_backend_docker.bat"
goto end

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
call npm run dev

:end
pause
