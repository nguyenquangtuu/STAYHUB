@echo off
chcp 65001 >nul
title [StayHub] Backend Docker Compose

echo ======================================================================
echo           STAYHUB - KHỞI ĐỘNG BACKEND & MYSQL BẰNG DOCKER
echo ======================================================================
echo.

set "DOCKER_APP1=%LOCALAPPDATA%\Programs\DockerDesktop\Docker Desktop.exe"
set "DOCKER_APP2=C:\Program Files\Docker\Docker\Docker Desktop.exe"

:: 1. Kiem tra Docker CLI
where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo [LOI] May tinh chua cai dat Docker CLI!
    echo Vui long cai dat Docker Desktop tai: https://www.docker.com/products/docker-desktop/
    pause
    exit /b 1
)

:: 2. Kiem tra Docker Daemon da chay chua
echo [1/3] Kiem tra Docker Daemon...
docker info >nul 2>nul
if %errorlevel% neq 0 (
    echo [..] Docker Daemon chua chay. Dang tu dong khoi dong Docker Desktop...
    if exist "%DOCKER_APP1%" (
        start "" "%DOCKER_APP1%"
    ) else if exist "%DOCKER_APP2%" (
        start "" "%DOCKER_APP2%"
    ) else (
        echo [CANH BAO] Vui long mo ung dung Docker Desktop bang tay tren Windows.
    )

    echo [..] Dang cho Docker khoi dong san sang (co the mat 10 - 25 giay)...
    :wait_docker
    timeout /t 3 /nobreak >nul
    docker info >nul 2>nul
    if %errorlevel% neq 0 (
        echo    ... van dang cho Docker Daemon khoi dong ...
        goto wait_docker
    )
)

echo [OK] Docker Daemon da san sang!
echo.

:: 3. Build va khoi dong MySQL 8.0 + Node.js API qua Docker Compose
echo [2/3] Dang khoi tao MySQL 8.0 va Build Backend Container...
cd /d "%~dp0"
docker compose up -d --build

if %errorlevel% neq 0 (
    echo.
    echo [LOI] Khong the khoi dong qua Docker Compose.
    pause
    exit /b 1
)

echo.
echo [3/3] Kiem tra trang thai cac container...
docker compose ps

echo.
echo ======================================================================
echo    BACKEND VA CSDL MYSQL DA HOAT DONG THANH CONG!
echo ======================================================================
echo  - Backend API:    http://localhost:5000
echo  - Health Check:   http://localhost:5000/api/health
echo  - MySQL Database: localhost:3306 (DB: stayhub_db, User: root)
echo.
echo  * Xem log truc tiep:  docker compose logs -f
echo  * Dung he thong:      docker compose down
echo ======================================================================
echo.
pause
