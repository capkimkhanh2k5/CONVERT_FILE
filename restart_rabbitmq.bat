@echo off
echo ========================================
echo Restarting RabbitMQ to Load Management Plugin
echo ========================================
echo.
echo This script requires Administrator privileges!
echo.

REM Check for admin rights
net session >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Please run as Administrator!
    echo Right-click this file ^> Run as administrator
    pause
    exit /b 1
)

echo [1/3] Stopping RabbitMQ service...
net stop RabbitMQ
timeout /t 3 >nul
echo.

echo [2/3] Setting ERLANG_HOME environment variable...
setx ERLANG_HOME "D:\Erlang OTP" /M
set ERLANG_HOME=D:\Erlang OTP
echo.

echo [3/3] Starting RabbitMQ service...
net start RabbitMQ
echo.

echo Waiting for RabbitMQ to fully start (15 seconds)...
timeout /t 15 >nul
echo.

echo ========================================
echo Testing Ports...
echo ========================================
netstat -ano | findstr "5672"
netstat -ano | findstr "15672"
echo.

echo ========================================
echo Opening Management UI...
echo ========================================
start http://localhost:15672
echo Login: guest / guest
echo.
pause
