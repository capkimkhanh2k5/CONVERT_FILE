@echo off
REM ========================================
REM   STOP TOMCAT SERVER
REM ========================================

set TOMCAT_HOME=C:\Program Files\apache-tomcat-10.1.49

if not exist "%TOMCAT_HOME%" (
    echo ERROR: Tomcat not found at %TOMCAT_HOME%
    pause
    exit /b 1
)

echo ========================================
echo   Stopping Tomcat Server...
echo ========================================
echo.

call "%TOMCAT_HOME%\bin\shutdown.bat"

echo.
echo ========================================
echo   Tomcat Server: STOPPED
echo ========================================
echo.

pause
