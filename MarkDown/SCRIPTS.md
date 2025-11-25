# 📜 SCRIPTS REFERENCE

Danh sách tất cả scripts và chức năng.

---

## 🗄️ MySQL Scripts

### `mysql_start.bat`
**Chức năng:** Khởi động MySQL service

```cmd
mysql_start.bat
```

- Service name: `MySQL95`
- Hiển thị host, port sau khi khởi động
- Tự động kiểm tra trạng thái

---

### `mysql_stop.bat`
**Chức năng:** Dừng MySQL service

```cmd
mysql_stop.bat
```

- Dừng service `MySQL95` 
- Kiểm tra trạng thái sau khi dừng

---

### `setup_database.bat`
**Chức năng:** Tạo database và import schema lần đầu

```cmd
setup_database.bat
```

**Yêu cầu:**
- MySQL đang chạy
- File `src/main/java/com/convertfile/model/bean/BD_Query.sql` tồn tại

**Thực hiện:**
1. Tạo database `file_converter`
2. Import schema (users, files, tasks tables)
3. Hiển thị danh sách tables

**Lưu ý:** Cần nhập password MySQL **3 lần**

---

## 🚀 Tomcat Scripts

### `tomcat_start.bat`
**Chức năng:** Khởi động Tomcat server và mở browser

```cmd
tomcat_start.bat
```

**Thực hiện:**
1. Kiểm tra Tomcat có tồn tại không
2. Chạy `startup.bat`
3. Đợi 5 giây để server khởi động
4. Mở browser tại http://localhost:8080/CONVERT_FILE/

**Biến môi trường:** `CATALINA_HOME = C:\Program Files\apache-tomcat-10.1.49`

---

### `tomcat_stop.bat`
**Chức năng:** Dừng Tomcat server

```cmd
tomcat_stop.bat
```

- Chạy `shutdown.bat` để dừng gracefully
- Đợi 5 giây để cleanup
- Không force kill process

---

## 📦 Deployment Scripts

### `deploy_standalone.bat`
**Chức năng:** Build và deploy application lên standalone Tomcat

```cmd
deploy_standalone.bat
```

**Quy trình:**
1. **Stop Tomcat** - Dừng server nếu đang chạy
2. **Clean deployment** - Xóa WAR và thư mục cũ
3. **Maven build** - `mvn clean package`
4. **Copy WAR** - Copy `CONVERT_FILE.war` vào `webapps/`
5. **Start Tomcat** - Khởi động server

**Thư mục deploy:** `C:\Program Files\apache-tomcat-10.1.49\webapps\CONVERT_FILE`

**Lưu ý:** Script này dành cho **standalone Tomcat**, không dùng cho XAMPP.

---

### `deploy.bat`
**Chức năng:** Build và deploy lên XAMPP Tomcat

```cmd
deploy.bat
```

**Quy trình:** Tương tự `deploy_standalone.bat` nhưng target là XAMPP Tomcat.

**Thư mục deploy:** `C:\xampp\tomcat\webapps\CONVERT_FILE`

---

## 🔍 Utilities

### `view_logs.bat`
**Chức năng:** Mở thư mục logs của Tomcat

```cmd
view_logs.bat
```

**Logs location:** `C:\Program Files\apache-tomcat-10.1.49\logs`

**Files quan trọng:**
- `catalina.out` / `catalina.{date}.log` - Server logs
- `localhost.{date}.log` - Application logs
- `manager.{date}.log` - Deployment logs

---

## 🔄 Workflow thông thường

### Lần đầu setup
```cmd
1. mysql_start.bat
2. setup_database.bat
3. deploy_standalone.bat
```

### Chạy hàng ngày
```cmd
1. mysql_start.bat
2. tomcat_start.bat
```

### Sau khi sửa code
```cmd
1. tomcat_stop.bat
2. deploy_standalone.bat
```

### Tắt máy
```cmd
1. tomcat_stop.bat
2. mysql_stop.bat
```

---

## ⚙️ Cấu hình Scripts

Tất cả scripts đều sử dụng các đường dẫn sau:

| Biến | Giá trị |
|------|---------|
| `TOMCAT_HOME` | `C:\Program Files\apache-tomcat-10.1.49` |
| MySQL Service | `MySQL95` |
| MySQL Port | `3306` |
| Tomcat Port | `8080` |
| Database Name | `file_converter` |
| WAR Name | `CONVERT_FILE.war` |

**Nếu đường dẫn khác:** Mở file `.bat` và sửa biến `TOMCAT_HOME` ở đầu file.

---

## 🐛 Troubleshooting

### Script không chạy được

**Nguyên nhân:** Không tìm thấy MySQL/Tomcat

**Giải pháp:**
```powershell
# Kiểm tra PATH
echo $env:Path

# Thêm MySQL vào PATH
[Environment]::SetEnvironmentVariable("Path", $env:Path + ";C:\Program Files\MySQL\MySQL Server 9.5\bin", "User")

# Thêm CATALINA_HOME
[Environment]::SetEnvironmentVariable("CATALINA_HOME", "C:\Program Files\apache-tomcat-10.1.49", "User")
```

### deploy_standalone.bat báo lỗi

**Lỗi:** `Maven build failed`

**Giải pháp:**
```cmd
# Clean build thủ công
mvn clean install

# Nếu vẫn lỗi, skip tests
mvn clean install -DskipTests
```

### tomcat_start.bat không mở browser

**Nguyên nhân:** Server chưa kịp khởi động

**Giải pháp:** Đợi thêm 10-15 giây rồi mở thủ công: http://localhost:8080/CONVERT_FILE/

---

## 📝 Ghi chú

- ✅ Scripts đã được tối ưu cho **Windows**
- ✅ Hỗ trợ **PowerShell** và **CMD**
- ✅ Tự động kiểm tra trạng thái services
- ✅ Có error handling và thông báo rõ ràng
- ⚠️ Chạy với quyền **Administrator** nếu gặp lỗi permission

---

**Tham khảo thêm:**
- [README.md](README.md) - Tài liệu đầy đủ
- [QUICKSTART.md](QUICKSTART.md) - Hướng dẫn chạy nhanh
