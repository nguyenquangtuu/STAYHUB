@echo off
chcp 65001 >nul
title [StayHub] Khoi Dong May Ao va Chay Ung Dung

echo ======================================================================
echo          STAYHUB - TỰ ĐỘNG MỞ MÁY ẢO VÀ CHẠY ỨNG DỤNG ANDROID
echo ======================================================================
echo.

set "ANDROID_SDK=%LOCALAPPDATA%\Android\Sdk"
set "EMULATOR=%ANDROID_SDK%\emulator\emulator.exe"
set "ADB=%ANDROID_SDK%\platform-tools\adb.exe"
set "AVD_NAME=Pixel_9"
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: 1. Kiem tra Android SDK
if not exist "%EMULATOR%" (
    echo [LOI] Khong tim thay emulator tai: "%EMULATOR%"
    echo Vui long kiem tra lai duong dan Android SDK.
    pause
    exit /b 1
)

:: 2. Kiem tra xem may ao da duoc mo chua
echo [1/4] Kiem tra may ao Android...
"%ADB%" devices | findstr "emulator-" >nul
if %errorlevel% equ 0 (
    echo [OK] May ao da dang chay san!
) else (
    echo [..] May ao chua bat. Dang khoi dong may ao %AVD_NAME%...
    start "" "%EMULATOR%" -avd %AVD_NAME%
    echo [..] Dang cho may ao ket noi...
    "%ADB%" wait-for-device
)

:: 3. Cho he dieu hanh Android boot hoan tat
echo [2/4] Cho he thong Android boot xong (sys.boot_completed)...
:wait_boot
timeout /t 2 /nobreak >nul
for /f "tokens=*" %%a in ('"%ADB%" shell getprop sys.boot_completed 2^>nul') do set BOOT=%%a
if not "%BOOT%"=="1" (
    echo    ... van dang khoi dong Android, vui long doi ...
    goto wait_boot
)
echo [OK] May ao da san sang!
echo.

:: 4. Bien dich va cai dat APK
echo [3/4] Dang bien dich va cai dat app vao may ao (gradlew installDebug)...
cd /d "%~dp0android"
call gradlew.bat installDebug
if %errorlevel% neq 0 (
    echo.
    echo [CANH BAO] Khong the build tu dong, dang thu mo truc tiep app neu da cai dat truoc do...
)

:: 5. Mo ung dung StayHub tren may ao
echo [4/4] Dang khoi chay ung dung StayHub tren man hinh may ao...
"%ADB%" shell am start -n com.stayhub.app/.ui.SplashActivity

echo.
echo ======================================================================
echo    DA MO MAY AO VA CHAY UNG DUNG STAYHUB THANH CONG!
echo ======================================================================
echo.
pause
