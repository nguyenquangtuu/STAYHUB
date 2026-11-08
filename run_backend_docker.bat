@echo off
title StayHub - Backend Docker Compose

echo ======================================================================
echo           STAYHUB - KHOI DONG BACKEND VA MYSQL BANG DOCKER
echo ======================================================================
echo.

set "DOCKER_APP1=%LOCALAPPDATA%\Programs\DockerDesktop\Docker Desktop.exe"
set "DOCKER_APP2=C:\Program Files\Docker\Docker\Docker Desktop.exe"

:: 1. Kiem tra Docker CLI
where docker >nul 2>nul
if errorlevel 1 (
    echo [LOI] May tinh chua cai dat Docker!
    echo Vui long cai dat Docker Desktop tai: https://www.docker.com/products/docker-desktop/
    goto end
)

:: 2. Kiem tra Docker Daemon
echo [1/3] Kiem tra Docker Daemon...
docker info >nul 2>nul
if not errorlevel 1 goto docker_ready

echo [..] Docker chua bat. Dang khoi dong Docker Desktop...
if exist "%DOCKER_APP1%" (
    start "" "%DOCKER_APP1%"
) else if exist "%DOCKER_APP2%" (
    start "" "%DOCKER_APP2%"
) else (
    echo [CANH BAO] Vui long mo Docker Desktop tu Start Menu.
)

echo [..] Dang cho Docker khoi dong san sang (khoang 15 - 30 giay)...

:wait_docker
timeout /t 3 /nobreak >nul
docker info >nul 2>nul
if errorlevel 1 (
    echo    ... van dang cho Docker khoi dong ...
    goto wait_docker
)

:docker_ready
echo [OK] Docker Daemon da san sang!
echo.

:: 3. Chay docker compose
echo [2/3] Dang khoi tao MySQL 8.0 va Build Backend Container...
cd /d "%~dp0"
docker compose up -d --build

if errorlevel 1 (
    echo.
    echo [LOI] Khong the khoi dong qua Docker Compose.
    goto end
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

:end
pause
