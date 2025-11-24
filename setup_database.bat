@echo off
REM ========================================
REM   SETUP DATABASE
REM ========================================

echo ========================================
echo   MySQL Database Setup
echo ========================================
echo.

echo This script will:
echo 1. Create database 'file_converter'
echo 2. Import schema from BD_Query.sql
echo 3. Grant permissions
echo.

set /p CONTINUE="Continue? (Y/N): "
if /i not "%CONTINUE%"=="Y" exit /b 0

echo.
echo Enter MySQL root password when prompted...
echo.

REM Create database first
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS file_converter CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

if %ERRORLEVEL% neq 0 (
    echo Failed to create database
    goto :failed
)

REM Import schema using database parameter
mysql -u root -p file_converter < "src\main\java\com\convertfile\model\bean\BD_Query.sql"

if %ERRORLEVEL% equ 0 (
    echo.
    echo Checking tables created...
    mysql -u root -p -e "USE file_converter; SHOW TABLES;"
)

if %ERRORLEVEL% equ 0 (
    echo.
    echo ========================================
    echo   Database Setup: SUCCESS
    echo ========================================
    echo   Database: file_converter
    echo   Tables created successfully
    echo ========================================
) else (
    echo.
    echo ========================================
    echo   Database Setup: FAILED
    echo ========================================
    echo   Please check:
    echo   1. MySQL is running
    echo   2. BD_Query.sql file exists
    echo   3. MySQL credentials are correct
    echo ========================================
)

echo.
pause
