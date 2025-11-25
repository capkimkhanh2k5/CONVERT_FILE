@echo off
echo ========================================
echo Checking Worker Status
echo ========================================
echo.

echo Checking database for waiting tasks...
mysql -uroot -pRmr2612+ -D file_converter -e "SELECT task_id, file_id, task_type, status, created_at FROM tasks WHERE status='WAITING' ORDER BY created_at DESC LIMIT 5;"
echo.

echo Checking all tasks...
mysql -uroot -pRmr2612+ -D file_converter -e "SELECT task_id, task_type, status, created_at FROM tasks ORDER BY created_at DESC LIMIT 10;"
echo.

echo ========================================
echo Worker Pool should be processing these tasks
echo If stuck, check Tomcat logs for worker errors
echo ========================================
pause
