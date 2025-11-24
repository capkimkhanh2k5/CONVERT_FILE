@echo off
REM ==========================================
REM Auto Deploy Script for CONVERT_FILE
REM ==========================================

echo ========================================
echo    AUTO DEPLOY - CONVERT_FILE
echo ========================================
echo.

REM Set paths
set PROJECT_DIR=C:\Users\Victus\CONVERT_FILE
set TOMCAT_DIR=D:\Downloads\apache-tomcat-10.1.49-windows-x64\apache-tomcat-10.1.49
set WAR_FILE=%PROJECT_DIR%\target\CONVERT_FILE.war
set DEPLOY_DIR=%TOMCAT_DIR%\webapps

echo [1/6] Building project with Maven...
cd /d "%PROJECT_DIR%"
call mvn clean package -DskipTests
if errorlevel 1 (
    echo ERROR: Maven build failed!
    pause
    exit /b 1
)
echo ✓ Build successful!
echo.

echo [2/6] Stopping Tomcat...
cd /d "%TOMCAT_DIR%\bin"
call shutdown.bat 2>nul
timeout /t 5 /nobreak >nul
echo ✓ Tomcat stopped
echo.

echo [3/6] Removing old deployment...
if exist "%DEPLOY_DIR%\CONVERT_FILE" (
    rmdir /s /q "%DEPLOY_DIR%\CONVERT_FILE"
    echo ✓ Old folder removed
) else (
    echo - No old folder found
)

if exist "%DEPLOY_DIR%\CONVERT_FILE.war" (
    del /f /q "%DEPLOY_DIR%\CONVERT_FILE.war"
    echo ✓ Old WAR removed
)
echo.

echo [4/6] Copying new WAR file...
copy /y "%WAR_FILE%" "%DEPLOY_DIR%\CONVERT_FILE.war"
if errorlevel 1 (
    echo ERROR: Failed to copy WAR file!
    pause
    exit /b 1
)
echo ✓ WAR file copied
echo.

echo [5/6] Starting Tomcat...
cd /d "%TOMCAT_DIR%\bin"
start "Tomcat" cmd /c startup.bat
timeout /t 3 /nobreak >nul
echo ✓ Tomcat started
echo.

echo [6/6] Deployment complete!
echo.
echo ========================================
echo    DEPLOYMENT SUCCESSFUL!
echo ========================================
echo.
echo Application URL: http://localhost:8080/CONVERT_FILE/
echo.
echo Waiting 10 seconds for deployment to complete...
timeout /t 10 /nobreak >nul
echo.
echo Opening browser...
start http://localhost:8080/CONVERT_FILE/
echo.
echo Opening log viewer...
start powershell -NoExit -Command "Get-Content 'D:\Downloads\apache-tomcat-10.1.49-windows-x64\apache-tomcat-10.1.49\logs\catalina.2025-11-21.log' -Wait -Tail 50"
echo.
echo Done! Press any key to exit...
pause >nul
