@echo off
REM ========================================
REM   START MYSQL SERVER
REM ========================================

echo ========================================
echo   Starting MySQL Server...
echo ========================================
echo.

REM Try to start MySQL95 (MySQL Server 9.5 service name)
net start MySQL95 2>nul

if %ERRORLEVEL% equ 0 (
    echo.
    echo ========================================
    echo   MySQL Server: STARTED
    echo ========================================
    echo   Host: localhost
    echo   Port: 3306
    echo ========================================
) else (
    echo.
    echo ========================================
    echo   Failed to start MySQL!
    echo ========================================
    echo.
    echo Possible reasons:
    echo 1. MySQL service name is different (not MySQL95)
    echo 2. MySQL is already running        
    echo 3. You need Administrator privileges
    echo.
    echo Check Services.msc for MySQL service name
    echo ========================================
)

echo.
pause
