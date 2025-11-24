@echo off
:: Deploy CONVERT_FILE to Tomcat (requires Administrator rights)
echo ========================================
echo   CONVERT_FILE Deployment Script
echo ========================================
echo.

:: Check if running as Administrator
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo [ERROR] This script requires Administrator privileges!
    echo Please right-click and select "Run as Administrator"
    pause
    exit /b 1
)

echo [1/4] Stopping Tomcat...
call tomcat_stop.bat
timeout /t 3 /nobreak >nul

echo [2/4] Removing old deployment...
del "C:\Program Files\apache-tomcat-10.1.49\webapps\CONVERT_FILE.war" 2>nul
rmdir /s /q "C:\Program Files\apache-tomcat-10.1.49\webapps\CONVERT_FILE" 2>nul

echo [3/4] Deploying new WAR...
copy /Y "target\CONVERT_FILE.war" "C:\Program Files\apache-tomcat-10.1.49\webapps\CONVERT_FILE.war"
if %errorLevel% neq 0 (
    echo [ERROR] Failed to copy WAR file!
    pause
    exit /b 1
)

echo [4/4] Starting Tomcat...
call tomcat_start.bat

echo.
echo ========================================
echo   Deployment Complete!
echo   Wait 10-20 seconds for Tomcat to deploy
echo   Then access: http://localhost:8080/CONVERT_FILE/
echo ========================================
pause
