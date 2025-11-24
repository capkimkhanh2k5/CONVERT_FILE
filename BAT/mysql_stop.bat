@echo off
REM ========================================
REM   STOP MYSQL SERVER
REM ========================================

echo ========================================
echo   Stopping MySQL Server...
echo ========================================
echo.

REM Try to stop MySQL95 (MySQL Server 9.5 service name)
net stop MySQL95 2>nul

if %ERRORLEVEL% equ 0 (
    echo.
    echo ========================================
    echo   MySQL Server: STOPPED
    echo ========================================
) else (
    echo.
    echo ========================================
    echo   Failed to stop MySQL!
    echo ========================================
    echo.
    echo Possible reasons:
    echo 1. MySQL service name is different (not MySQL80)
    echo 2. MySQL is already stopped
    echo 3. You need Administrator privileges
    echo ========================================
)

echo.
pause
