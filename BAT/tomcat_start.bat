@echo off
REM ========================================
REM   START TOMCAT SERVER
REM ========================================

set TOMCAT_HOME=C:\Program Files\apache-tomcat-10.1.49

if not exist "%TOMCAT_HOME%" (
    echo ERROR: Tomcat not found at %TOMCAT_HOME%
    echo Please install Tomcat or update TOMCAT_HOME path
    pause
    exit /b 1
)

echo ========================================
echo   Starting Tomcat Server...
echo ========================================
echo.

call "%TOMCAT_HOME%\bin\startup.bat"

echo.
echo ========================================
echo   Tomcat Server: STARTING
echo ========================================
echo   URL: http://localhost:8080/
echo   Manager: http://localhost:8080/manager/html
echo ========================================
echo.
echo Tomcat is starting in background...
echo Check logs at: %TOMCAT_HOME%\logs
echo.

timeout /t 3 /nobreak >nul
start http://localhost:8080/

pause
