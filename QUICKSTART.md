# 🚀 HƯỚNG DẪN CHẠY NHANH

## Cài đặt lần đầu

### 1️⃣ Cài MySQL Server 9.5
- Download: https://dev.mysql.com/downloads/mysql/
- Cài với service name `MySQL95`
- Nhớ password root

### 2️⃣ Cài Apache Tomcat 10.1.49  
- Download: https://tomcat.apache.org/download-10.cgi
- Giải nén vào `C:\Program Files\apache-tomcat-10.1.49`

### 3️⃣ Thiết lập PATH (PowerShell - Admin)

```powershell
# MySQL
[Environment]::SetEnvironmentVariable("Path", $env:Path + ";C:\Program Files\MySQL\MySQL Server 9.5\bin", "User")

# Tomcat
[Environment]::SetEnvironmentVariable("CATALINA_HOME", "C:\Program Files\apache-tomcat-10.1.49", "User")
[Environment]::SetEnvironmentVariable("Path", $env:Path + ";C:\Program Files\apache-tomcat-10.1.49\bin", "User")
```

**⚠️ SAU KHI SET PATH: Đóng terminal và mở lại terminal mới!**

---

## Chạy lần đầu

### Bước 1: Start MySQL
```cmd
mysql_start.bat
```

### Bước 2: Tạo Database
```cmd
setup_database.bat
```
- Nhập password MySQL (3 lần)
- Xem có hiện bảng `users`, `files`, `tasks` không

### Bước 3: Cấu hình Application

Mở file `src/main/resources/application.properties`:

```properties
# Thay password MySQL của bạn
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Cloudinary (optional - nếu muốn upload lên cloud)
cloudinary.api_key=YOUR_KEY
cloudinary.api_secret=YOUR_SECRET
```

### Bước 4: Deploy

```cmd
deploy_standalone.bat
```

Đợi Maven build xong (có thể mất vài phút lần đầu).

### Bước 5: Truy cập

Mở trình duyệt: **http://localhost:8080/CONVERT_FILE/**

---

## Chạy hàng ngày

```cmd
# 1. Start MySQL
mysql_start.bat

# 2. Start Tomcat  
tomcat_start.bat

# Trình duyệt sẽ tự động mở
```

### Dừng server

```cmd
# 1. Stop Tomcat
tomcat_stop.bat

# 2. Stop MySQL
mysql_stop.bat
```

---

## Nếu có lỗi

### ❌ MySQL không chạy được
```powershell
# Kiểm tra service
Get-Service MySQL95

# Restart
net stop MySQL95
net start MySQL95
```

### ❌ Port 8080 đã bị chiếm
```cmd
# Tìm process
netstat -ano | findstr :8080

# Kill process (thay <PID>)
taskkill /PID <PID> /F
```

### ❌ Build lỗi
```cmd
mvn clean install
```

### ❌ Xem logs
```cmd
view_logs.bat
```

---

## Các file .bat quan trọng

| File | Làm gì |
|------|--------|
| `mysql_start.bat` | Bật MySQL |
| `mysql_stop.bat` | Tắt MySQL |
| `setup_database.bat` | Tạo DB lần đầu |
| `tomcat_start.bat` | Bật Tomcat + mở browser |
| `tomcat_stop.bat` | Tắt Tomcat |
| `deploy_standalone.bat` | Build + Deploy app |
| `view_logs.bat` | Xem logs debug |

---

## Test chức năng

1. Truy cập: http://localhost:8080/CONVERT_FILE/
2. Click **Browse** → Chọn file `.csv`
3. Chọn format: **CSV to JSON**
4. Click **Upload & Convert**
5. Chờ xử lý xong
6. Thấy file xuất hiện trong **Recent Conversions**
7. Click **Download** để tải về

---

## Tips

✅ Chạy `mysql_start.bat` trước khi mở Tomcat

✅ Nếu sửa code Java, chạy lại `deploy_standalone.bat`

✅ Nếu sửa JSP, chỉ cần refresh browser (không cần build lại)

✅ Session cookies chỉ hoạt động trên `localhost`, không test trên `127.0.0.1`

✅ Check logs nếu có lỗi: `view_logs.bat`

---

**Chúc code vui vẻ! 🎉**
