@echo off
REM ========================================
REM   VIEW TOMCAT LOGS
REM ========================================

set TOMCAT_HOME=C:\Program Files\apache-tomcat-10.1.49
set LOG_DIR=%TOMCAT_HOME%\logs

if not exist "%LOG_DIR%" (
    echo ERROR: Tomcat logs directory not found
    pause
    exit /b 1
)

echo ========================================
echo   Tomcat Logs Location
echo ========================================
echo   %LOG_DIR%
echo ========================================
echo.

echo Recent log files:
dir /b /o-d "%LOG_DIR%\*.log" 2>nul | findstr /n "^"
dir /b /o-d "%LOG_DIR%\*.txt" 2>nul | findstr /n "^"

echo.
echo Opening logs folder...
start "" "%LOG_DIR%"

echo.
echo To view catalina.out (main log):
echo   notepad "%LOG_DIR%\catalina.out"
echo.
echo To view latest localhost log:
echo   notepad "%LOG_DIR%\localhost.*.log"
echo.

pause
