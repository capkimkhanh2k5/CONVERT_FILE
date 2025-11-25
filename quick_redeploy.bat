@echo off
echo ========================================
echo Quick Redeploy (Phase 2 Mode)
echo ========================================
echo.

REM Find running Tomcat
echo Checking for running Tomcat...
for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq java.exe" /FO LIST ^| findstr "PID:"') do (
    echo Found Java process: %%a
    netstat -ano | findstr "%%a" | findstr "8080" >nul
    if !ERRORLEVEL! EQU 0 (
        echo Stopping Tomcat (PID %%a)...
        taskkill /F /PID %%a
        timeout /t 3 >nul
    )
)

echo.
echo Waiting for port 8080 to be free...
timeout /t 5 >nul

echo.
echo Starting Tomcat...
cd /d "%CD%"
call tomcat_start.bat

echo.
echo ========================================
echo Deployment Complete!
echo ========================================
echo.
echo Application running in Phase 2 mode:
echo - Database polling (1s interval)
echo - WebSocket real-time updates
echo - HikariCP connection pool
echo - 5 worker threads
echo.
echo RabbitMQ and Redis are disabled for now
echo.
pause
