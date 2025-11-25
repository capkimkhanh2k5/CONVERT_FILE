@echo off
echo ========================================
echo Testing RabbitMQ and Redis Infrastructure
echo ========================================
echo.

REM Set ERLANG_HOME for this session
set ERLANG_HOME=D:\Erlang OTP

echo [1/4] Testing Redis Connection...
D:\Redis\redis-cli.exe ping
if %ERRORLEVEL% EQU 0 (
    echo [OK] Redis is running
) else (
    echo [FAILED] Redis is not responding
)
echo.

echo [2/4] Checking RabbitMQ Service...
sc query RabbitMQ | findstr "RUNNING"
if %ERRORLEVEL% EQU 0 (
    echo [OK] RabbitMQ service is running
) else (
    echo [WARNING] RabbitMQ service is not running
    echo Run as Administrator: net start RabbitMQ
)
echo.

echo [3/4] Testing RabbitMQ Management UI...
echo Opening http://localhost:15672
echo Default credentials: guest / guest
timeout /t 2 >nul
start http://localhost:15672
echo.

echo [4/4] Checking RabbitMQ Status...
"D:\RabbitMQ Server\rabbitmq_server-4.2.1\sbin\rabbitmqctl.bat" status
if %ERRORLEVEL% EQU 0 (
    echo [OK] RabbitMQ is responding
) else (
    echo [INFO] RabbitMQ might need restart to load management plugin
)
echo.

echo ========================================
echo Test Complete!
echo ========================================
echo.
echo Next Steps:
echo 1. If RabbitMQ management UI opened, login with guest/guest
echo 2. Run: mvn clean package
echo 3. Deploy to Tomcat and test file conversion
echo.
pause
