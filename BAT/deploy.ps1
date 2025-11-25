# Deploy script with version tracking
# Run as Administrator

$ErrorActionPreference = "Continue"

Write-Host "🚀 ConvertFile Deployment Script" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""

# Configuration
$TOMCAT_DIR = "C:\Program Files\apache-tomcat-10.1.49"
$WAR_SOURCE = "C:\Users\Victus\CONVERT_FILE\target\CONVERT_FILE.war"
$WAR_DEST = "$TOMCAT_DIR\webapps\CONVERT_FILE.war"
$APP_DIR = "$TOMCAT_DIR\webapps\CONVERT_FILE"

# Step 1: Stop Tomcat
Write-Host "⏹️  Stopping Tomcat..." -ForegroundColor Yellow
$javaProcesses = Get-Process -Name java -ErrorAction SilentlyContinue
if ($javaProcesses) {
    Stop-Process -Name java -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
    Write-Host "✅ Tomcat stopped" -ForegroundColor Green
} else {
    Write-Host "ℹ️  Tomcat was not running" -ForegroundColor Gray
}

# Step 2: Remove old deployment
Write-Host ""
Write-Host "🗑️  Removing old deployment..." -ForegroundColor Yellow
if (Test-Path $APP_DIR) {
    Remove-Item $APP_DIR -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "✅ Old app directory removed" -ForegroundColor Green
}
if (Test-Path $WAR_DEST) {
    Remove-Item $WAR_DEST -Force -ErrorAction SilentlyContinue
    Write-Host "✅ Old WAR file removed" -ForegroundColor Green
}

# Step 3: Check if new WAR exists
Write-Host ""
if (-not (Test-Path $WAR_SOURCE)) {
    Write-Host "❌ WAR file not found: $WAR_SOURCE" -ForegroundColor Red
    Write-Host "   Please build the project first: mvn clean package" -ForegroundColor Yellow
    exit 1
}

# Step 4: Get WAR file info
$warInfo = Get-Item $WAR_SOURCE
$warSize = [math]::Round($warInfo.Length / 1MB, 2)
$warTime = $warInfo.LastWriteTime.ToString("yyyy-MM-dd HH:mm:ss")

Write-Host "📦 WAR File Info:" -ForegroundColor Cyan
Write-Host "   Path: $WAR_SOURCE" -ForegroundColor Gray
Write-Host "   Size: $warSize MB" -ForegroundColor Gray
Write-Host "   Built: $warTime" -ForegroundColor Gray

# Step 5: Copy new WAR
Write-Host ""
Write-Host "📋 Copying new WAR file..." -ForegroundColor Yellow
Copy-Item $WAR_SOURCE $WAR_DEST -Force
Write-Host "✅ WAR file copied" -ForegroundColor Green

# Step 6: Start Tomcat
Write-Host ""
Write-Host "▶️  Starting Tomcat..." -ForegroundColor Yellow
Set-Location "$TOMCAT_DIR\bin"
Start-Process -FilePath ".\startup.bat" -WindowStyle Normal

# Step 7: Wait for deployment
Write-Host "⏳ Waiting for deployment..." -ForegroundColor Yellow
$maxWait = 30
$waited = 0
while ($waited -lt $maxWait) {
    Start-Sleep -Seconds 1
    $waited++
    if (Test-Path $APP_DIR) {
        Write-Host "✅ Application deployed!" -ForegroundColor Green
        break
    }
    Write-Host "   Waiting... ($waited/$maxWait seconds)" -ForegroundColor Gray
}

# Step 8: Check version endpoint
Write-Host ""
Write-Host "🔍 Checking application version..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/CONVERT_FILE/version" -Method Get -TimeoutSec 10
    Write-Host ""
    Write-Host "✅ Application is running!" -ForegroundColor Green
    Write-Host "   Version: $($response.version)" -ForegroundColor Cyan
    Write-Host "   Build Time: $($response.buildTime)" -ForegroundColor Gray
    Write-Host "   Deploy Time: $($response.deployTime)" -ForegroundColor Gray
    Write-Host "   Java Version: $($response.javaVersion)" -ForegroundColor Gray
    Write-Host "   Tomcat Version: $($response.tomcatVersion)" -ForegroundColor Gray
} catch {
    Write-Host "⚠️  Could not reach version endpoint yet" -ForegroundColor Yellow
    Write-Host "   The application might still be starting up..." -ForegroundColor Gray
}

# Step 9: Open browser
Write-Host ""
Write-Host "🌐 Opening application in browser..." -ForegroundColor Cyan
Start-Sleep -Seconds 2
Start-Process "http://localhost:8080/CONVERT_FILE/home"

Write-Host ""
Write-Host "=================================" -ForegroundColor Cyan
Write-Host "✅ Deployment Complete!" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "📝 Useful URLs:" -ForegroundColor Cyan
Write-Host "   App: http://localhost:8080/CONVERT_FILE/home" -ForegroundColor Gray
Write-Host "   Version: http://localhost:8080/CONVERT_FILE/version" -ForegroundColor Gray
Write-Host "   Logs: $TOMCAT_DIR\logs\catalina.out" -ForegroundColor Gray
Write-Host ""
