# Quick version check script
Write-Host "🔍 Checking ConvertFile Version..." -ForegroundColor Cyan
Write-Host ""

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/CONVERT_FILE/version" -Method Get -TimeoutSec 5
    
    Write-Host "✅ Application Status: " -NoNewline -ForegroundColor Green
    Write-Host "RUNNING" -ForegroundColor Green
    Write-Host ""
    Write-Host "📦 Version Info:" -ForegroundColor Cyan
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
    Write-Host "   Version:        " -NoNewline -ForegroundColor Gray
    Write-Host $response.version -ForegroundColor White
    Write-Host "   Build Time:     " -NoNewline -ForegroundColor Gray
    Write-Host $response.buildTime -ForegroundColor White
    Write-Host "   Deploy Time:    " -NoNewline -ForegroundColor Gray
    Write-Host $response.deployTime -ForegroundColor White
    Write-Host "   Java Version:   " -NoNewline -ForegroundColor Gray
    Write-Host $response.javaVersion -ForegroundColor White
    Write-Host "   Tomcat Version: " -NoNewline -ForegroundColor Gray
    Write-Host $response.tomcatVersion -ForegroundColor White
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
    
} catch {
    Write-Host "❌ Application Status: " -NoNewline -ForegroundColor Red
    Write-Host "NOT RUNNING" -ForegroundColor Red
    Write-Host ""
    Write-Host "⚠️  Cannot connect to http://localhost:8080/CONVERT_FILE/version" -ForegroundColor Yellow
    Write-Host "   Please check if Tomcat is running" -ForegroundColor Gray
}

Write-Host ""
