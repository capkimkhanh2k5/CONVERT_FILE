# 🚀 ConvertFile - Deployment Guide

## Cách Deploy Nhanh (Khuyên dùng)

### 1. Build project
```powershell
mvn clean package -DskipTests
```

### 2. Deploy với script tự động (Run as Administrator)
```powershell
.\deploy.ps1
```

Script này sẽ:
- ⏹️ Stop Tomcat
- 🗑️ Xóa deployment cũ
- 📋 Copy WAR file mới
- ▶️ Start Tomcat
- 🔍 Kiểm tra version
- 🌐 Mở browser tự động

### 3. Kiểm tra version đang chạy
```powershell
.\check-version.ps1
```

Hoặc truy cập: http://localhost:8080/CONVERT_FILE/version

---

## Cách Deploy Thủ Công (Legacy)

```powershell
# Run as Administrator

# Stop Tomcat
Stop-Process -Name java -Force

# Remove old deployment
Remove-Item "C:\Program Files\apache-tomcat-10.1.49\webapps\CONVERT_FILE" -Recurse -Force
Remove-Item "C:\Program Files\apache-tomcat-10.1.49\webapps\CONVERT_FILE.war" -Force

# Copy new WAR
Copy-Item "C:\Users\Victus\CONVERT_FILE\target\CONVERT_FILE.war" "C:\Program Files\apache-tomcat-10.1.49\webapps\"

# Start Tomcat
cd "C:\Program Files\apache-tomcat-10.1.49\bin"
.\startup.bat
```

---

## Kiểm tra Version đang chạy

### Cách 1: Script PowerShell
```powershell
.\check-version.ps1
```

### Cách 2: API Endpoint
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/CONVERT_FILE/version"
```

### Cách 3: Browser
Mở: http://localhost:8080/CONVERT_FILE/version

### Cách 4: Console Log (F12 Developer Tools)
Mở trang home, xem console sẽ thấy:
```
📦 App Version: 2.0.0
🔨 Build Time: 2025-11-24 18:32:33
🚀 Deploy Time: 2025-11-24 18:35:00
```

---

## Version Info JSON Response

```json
{
  "version": "2.0.0",
  "buildTime": "2025-11-24 18:32:33",
  "deployTime": "2025-11-24 18:35:00",
  "javaVersion": "21.0.9",
  "tomcatVersion": "Apache Tomcat/10.1.49"
}
```

---

## Troubleshooting

### Tomcat không stop được
```powershell
# Force kill tất cả Java processes
Get-Process -Name java | Stop-Process -Force
```

### WAR không deploy
```powershell
# Check Tomcat logs
Get-Content "C:\Program Files\apache-tomcat-10.1.49\logs\catalina.out" -Tail 50
```

### Port 8080 bị chiếm
```powershell
# Find process using port 8080
netstat -ano | findstr :8080

# Kill process by PID
taskkill /PID <PID> /F
```

---

## Useful Commands

```powershell
# View Tomcat logs
Get-Content "C:\Program Files\apache-tomcat-10.1.49\logs\catalina.out" -Tail 50 -Wait

# Check if Tomcat is running
Get-Process -Name java -ErrorAction SilentlyContinue

# Check WAR file size
Get-Item "C:\Users\Victus\CONVERT_FILE\target\CONVERT_FILE.war" | Select-Object Name, Length, LastWriteTime
```

---

## Current Version

**Version:** 2.0.0
- ✅ 15 Conversion services tested (96/96 tests passing)
- ✅ Responsive UI with dynamic conversion options
- ✅ Version tracking system
- ✅ Automated deployment scripts
