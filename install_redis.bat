@echo off
REM ============================================
REM REDIS INSTALLATION SCRIPT
REM ============================================
REM Prerequisites: Chocolatey package manager
REM Install Chocolatey: https://chocolatey.org/install
REM ============================================

echo ========================================
echo Installing Redis
echo ========================================

REM Check if running as Administrator
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo ERROR: This script must be run as Administrator!
    echo Right-click and select "Run as administrator"
    pause
    exit /b 1
)

REM Install Redis
echo.
echo Installing Redis...
choco install redis-64 -y

REM Start Redis Service
echo.
echo Starting Redis Service...
redis-server --service-install
redis-server --service-start

REM Test Redis
echo.
echo Testing Redis connection...
redis-cli ping

echo.
echo ========================================
echo Installation Complete!
echo ========================================
echo.
echo Redis Server: localhost:6379
echo Test command: redis-cli ping
echo Expected response: PONG
echo.
pause

exit /b 0
