# Script để đếm số records trong file JSON array
param(
    [string]$jsonPath
)

if (-not (Test-Path $jsonPath)) {
    Write-Host "File không tồn tại: $jsonPath" -ForegroundColor Red
    exit 1
}

try {
    $jsonContent = Get-Content $jsonPath -Raw | ConvertFrom-Json
    
    if ($jsonContent -is [Array]) {
        $recordCount = $jsonContent.Count
        Write-Host "✅ File JSON chứa: $recordCount records" -ForegroundColor Green
        
        # Hiển thị 3 records đầu và cuối
        Write-Host "`n📋 3 records đầu tiên:" -ForegroundColor Cyan
        $jsonContent[0..2] | ConvertTo-Json -Depth 2 -Compress
        
        if ($recordCount -gt 3) {
            Write-Host "`n📋 3 records cuối cùng:" -ForegroundColor Cyan
            $jsonContent[($recordCount-3)..($recordCount-1)] | ConvertTo-Json -Depth 2 -Compress
        }
    } else {
        Write-Host "⚠️ File JSON không phải là array!" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Lỗi khi đọc JSON: $_" -ForegroundColor Red
}
