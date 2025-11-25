@echo off
REM ============================================
REM RABBITMQ INSTALLATION SCRIPT
REM ============================================
REM Prerequisites: Chocolatey package manager
REM Install Chocolatey: https://chocolatey.org/install
REM ============================================

echo ========================================
echo Installing RabbitMQ and Dependencies
echo ========================================

REM Check if running as Administrator
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo ERROR: This script must be run as Administrator!
    echo Right-click and select "Run as administrator"
    pause
    exit /b 1
)

REM Install Erlang (RabbitMQ dependency)
echo.
echo [1/3] Installing Erlang...
choco install erlang -y

REM Install RabbitMQ
echo.
echo [2/3] Installing RabbitMQ...
choco install rabbitmq -y

REM Enable RabbitMQ Management Plugin
echo.
echo [3/3] Enabling RabbitMQ Management Plugin...
"C:\Program Files\RabbitMQ Server\rabbitmq_server-3.13.0\sbin\rabbitmq-plugins.bat" enable rabbitmq_management

REM Start RabbitMQ Service
echo.
echo Starting RabbitMQ Service...
net start RabbitMQ

echo.
echo ========================================
echo Installation Complete!
echo ========================================
echo.
echo RabbitMQ Management UI: http://localhost:15672
echo Default credentials:
echo   Username: guest
echo   Password: guest
echo.
echo Press any key to open Management UI in browser...
pause
start http://localhost:15672

exit /b 0
