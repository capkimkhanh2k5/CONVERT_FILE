@echo off
echo ========================================
echo Auto Restart Tomcat with Live Logs
echo ========================================
echo.

echo [1/4] Stopping Tomcat...
tasklist /FI "IMAGENAME eq java.exe" /FO LIST | findstr "PID:" >nul
if %ERRORLEVEL% EQU 0 (
    echo Killing Java processes...
    taskkill /F /IM java.exe >nul 2>&1
    timeout /t 3 >nul
    echo Tomcat stopped
) else (
    echo No Tomcat process found
)
echo.

echo [2/4] Cleaning deployment...
if exist "target\CONVERT_FILE" rmdir /s /q "target\CONVERT_FILE"
echo.

echo [3/4] Starting Tomcat...
start /min cmd /c "cd /d %CD% && tomcat_start.bat"
echo Waiting for Tomcat to start (15 seconds)...
timeout /t 15 >nul
echo.

echo [4/4] Checking startup logs...
echo ========================================
echo.

REM Try to find and display logs
set LOG_FOUND=0

REM Check workspace logs
if exist "logs\localhost.*.log" (
    for /f %%i in ('dir /b /o-d logs\localhost.*.log 2^>nul') do (
        echo Latest log: logs\%%i
        echo.
        echo === LAST 30 LINES ===
        powershell -Command "Get-Content 'logs\%%i' -Tail 30"
        set LOG_FOUND=1
        goto :log_done
    )
)

REM Check for catalina log
if exist "logs\catalina.out" (
    echo Latest log: logs\catalina.out
    echo.
    echo === LAST 30 LINES ===
    powershell -Command "Get-Content 'logs\catalina.out' -Tail 30"
    set LOG_FOUND=1
    goto :log_done
)

:log_done
if %LOG_FOUND% EQU 0 (
    echo WARNING: No logs found in workspace
    echo Check Tomcat installation logs folder
)

echo.
echo ========================================
echo Checking for Worker Pool startup...
echo ========================================
findstr /C:"Worker Pool" /C:"APP LISTENER" logs\*.log 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [WARNING] Worker Pool logs not found!
    echo This means:
    echo   1. AppListener not triggered
    echo   2. Workers failed to start
    echo   3. Check logs folder for errors
)
echo.

echo ========================================
echo Checking application status...
echo ========================================
echo.
echo Testing http://localhost:8080/CONVERT_FILE
timeout /t 2 >nul
curl -s -o nul -w "HTTP Status: %%{http_code}\n" http://localhost:8080/CONVERT_FILE
echo.

echo Opening application in browser...
start http://localhost:8080/CONVERT_FILE
echo.

echo ========================================
echo Restart Complete!
echo ========================================
echo.
echo If workers still not running:
echo   1. Check logs folder for exceptions
echo   2. Verify database connection
echo   3. Check if port 8080 is accessible
echo.
echo Press any key to view live logs (Ctrl+C to exit)...
pause >nul

echo.
echo === LIVE LOG TAIL (Ctrl+C to stop) ===
echo.
powershell -Command "Get-Content 'logs\localhost.*.log' -Wait -Tail 20 2>$null"
