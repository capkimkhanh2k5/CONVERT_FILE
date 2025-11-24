@echo off
REM ========================================
REM   DEPLOY TO STANDALONE TOMCAT
REM ========================================

echo ========================================
echo   DEPLOY TO STANDALONE TOMCAT
echo ========================================
echo.

REM Tomcat path - CHANGE THIS if your Tomcat is elsewhere
set TOMCAT_HOME=C:\Program Files\apache-tomcat-10.1.49
set WEBAPP_DIR=%TOMCAT_HOME%\webapps
set DEPLOY_DIR=%WEBAPP_DIR%\CONVERT_FILE

REM Check if Tomcat exists
if not exist "%TOMCAT_HOME%" (
    echo ERROR: Tomcat not found at %TOMCAT_HOME%
    echo Please install Tomcat 10 or update TOMCAT_HOME path
    pause
    exit /b 1
)

echo [1/5] Stopping Tomcat...
call "%TOMCAT_HOME%\bin\shutdown.bat" 2>nul
timeout /t 5 /nobreak >nul

echo [2/5] Cleaning old deployment...
if exist "%DEPLOY_DIR%" (
    rmdir /s /q "%DEPLOY_DIR%"
    echo    - Removed old webapp folder
)
if exist "%WEBAPP_DIR%\CONVERT_FILE.war" (
    del /f /q "%WEBAPP_DIR%\CONVERT_FILE.war"
    echo    - Removed old WAR file
)

echo [3/5] Building project with Maven...
call mvn clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo.
    echo ========================================
    echo   BUILD FAILED!
    echo ========================================
    pause
    exit /b 1
)

echo [4/5] Deploying WAR file...
if not exist "target\CONVERT_FILE.war" (
    echo ERROR: WAR file not found in target directory!
    pause
    exit /b 1
)

copy /y "target\CONVERT_FILE.war" "%WEBAPP_DIR%\"
echo    - Copied WAR to Tomcat webapps

echo [5/5] Starting Tomcat...
call "%TOMCAT_HOME%\bin\startup.bat"

echo.
echo ========================================
echo   DEPLOYMENT COMPLETE!
echo ========================================
echo   Application URL: http://localhost:8080/CONVERT_FILE/
echo   Tomcat Manager: http://localhost:8080/manager/html
echo.
echo   Wait 10-15 seconds for Tomcat to deploy the WAR file
echo ========================================

timeout /t 3 /nobreak >nul
start http://localhost:8080/CONVERT_FILE/

pause
