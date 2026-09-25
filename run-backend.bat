@echo off
title BookNest Backend Server
echo ========================================================
echo Starting BookNest Spring Boot Backend (Port 8080)
echo ========================================================
echo.

cd /d "%~dp0backend"

if "%DB_PASSWORD%"=="" (
    echo Note: DB_PASSWORD environment variable is not set.
    echo If your MySQL root user has a password, enter it below.
    echo If your root user has NO password, simply press Enter.
    set /p DB_PASSWORD="Enter MySQL Root Password: "
)

echo.
echo Starting application...
java -jar target\booknest-backend-1.0.0.jar

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Spring Boot backend failed to start.
    echo Please verify MySQL is running and your DB_PASSWORD is correct.
    pause
)
