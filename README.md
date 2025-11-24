# 📁 CONVERT_FILE

Web application chuyển đổi file với Java Servlet, JSP, MySQL và Cloudinary.

---

## 🚀 Tính năng

- ✅ **PDF ↔ DOCX** - Chuyển đổi qua lại giữa PDF và Word
- ✅ **Image → PDF** - Chuyển ảnh (JPG, PNG) thành PDF  
- ✅ **CSV → JSON** - Chuyển đổi dữ liệu CSV sang JSON
- ✅ **Cloud Storage** - Upload file lên Cloudinary tự động
- ✅ **Lịch sử** - Theo dõi các file đã chuyển đổi
- ✅ **Background Processing** - Xử lý bất đồng bộ không block UI

---

## 📋 Yêu cầu

- **Java JDK**: 21+
- **Maven**: 3.6+
- **MySQL Server**: 9.3/9.5
- **Apache Tomcat**: 10.1.49
- **Windows OS**

---

## ⚙️ Hướng dẫn Cài đặt

### Bước 1: Cài đặt MySQL Server

1. Download từ: https://dev.mysql.com/downloads/mysql/
2. Cài đặt với service name: `MySQL95`
3. Đặt password cho root
4. Thêm MySQL vào PATH:

```powershell
[Environment]::SetEnvironmentVariable("Path", $env:Path + ";C:\Program Files\MySQL\MySQL Server 9.5\bin", "User")
```

### Bước 2: Cài đặt Apache Tomcat

1. Download từ: https://tomcat.apache.org/download-10.cgi
2. Giải nén vào: `C:\Program Files\apache-tomcat-10.1.49`
3. Thiết lập biến môi trường:

```powershell
[Environment]::SetEnvironmentVariable("CATALINA_HOME", "C:\Program Files\apache-tomcat-10.1.49", "User")
[Environment]::SetEnvironmentVariable("Path", $env:Path + ";C:\Program Files\apache-tomcat-10.1.49\bin", "User")
```

**⚠️ Lưu ý:** Sau khi set biến môi trường, đóng và mở lại terminal mới.

### Bước 3: Khởi động MySQL

```cmd
mysql_start.bat
```

### Bước 4: Tạo Database

```cmd
setup_database.bat
```

Nhập password MySQL khi được hỏi (3 lần).

### Bước 5: Cấu hình Application

Mở file `src/main/resources/application.properties` và cập nhật:

```properties
# Cloudinary
cloudinary.cloud_name=davtsqowt
cloudinary.api_key=YOUR_API_KEY
cloudinary.api_secret=YOUR_API_SECRET

# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/file_converter
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Bước 6: Deploy Application

**Standalone mode (khuyên dùng):**
```cmd
deploy_standalone.bat
```

**XAMPP mode:**
```cmd
deploy.bat
```

---

## 🎯 Sử dụng

### Khởi động server

```cmd
tomcat_start.bat
```

Truy cập: **http://localhost:8080/CONVERT_FILE/**

### Dừng server

```cmd
tomcat_stop.bat
mysql_stop.bat
```

### Xem logs

```cmd
view_logs.bat
```

---

## 📁 Cấu trúc Project

```
CONVERT_FILE/
│
├── src/main/
│   ├── java/com/convertfile/
│   │   ├── controller/          # Servlets (Upload, Auth, Jobs)
│   │   ├── service/             # Business logic
│   │   │   ├── ConvertService/  # Conversion services
│   │   │   └── CloudService/    # Cloudinary integration
│   │   ├── model/               # DAO, Entities, DB
│   │   ├── worker/              # FileWorker (background thread)
│   │   ├── config/              # Configuration
│   │   └── bo/                  # Business Objects
│   │
│   ├── resources/
│   │   └── application.properties
│   │
│   └── webapp/
│       ├── index.jsp            # Landing page
│       ├── home.jsp             # Dashboard
│       ├── upload.jsp           # Upload form
│       ├── jobs.jsp             # Job history
│       ├── auth.jsp             # Login/Register
│       └── WEB-INF/
│           ├── web.xml
│           └── META-INF/context.xml
│
├── convertfile-core/            # Core library (reusable)
│   └── src/main/java/.../service/
│       └── csv_to_json_service.java
│
├── convertfile-tests/           # Unit tests
│   └── src/test/java/.../service/
│       └── CsvToJsonServiceTest.java
│
├── target/                      # Build output
│   └── CONVERT_FILE/            # WAR contents
│
├── pom.xml                      # Maven config
│
└── *.bat                        # Deployment scripts
```

---

## 🛠️ Scripts & Commands

| Script | Chức năng |
|--------|-----------|
| `mysql_start.bat` | Khởi động MySQL service (MySQL95) |
| `mysql_stop.bat` | Dừng MySQL service |
| `setup_database.bat` | Tạo database `file_converter` và import schema |
| `tomcat_start.bat` | Khởi động Tomcat server |
| `tomcat_stop.bat` | Dừng Tomcat server |
| `deploy_standalone.bat` | Build & deploy to standalone Tomcat |
| `deploy.bat` | Deploy to XAMPP Tomcat |
| `view_logs.bat` | Mở thư mục logs của Tomcat |

---

## 🧪 Testing

Chạy unit tests cho CSV to JSON service:

```cmd
cd convertfile-tests
mvn test
```

Kết quả mong đợi:
```
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
```

---

## 🔧 Troubleshooting

### ❌ MySQL không kết nối được

```powershell
# Kiểm tra service
Get-Service MySQL95

# Restart service
net stop MySQL95
net start MySQL95

# Test connection
mysql -u root -p -e "SELECT VERSION();"
```

### ❌ Tomcat lỗi "Port 8080 already in use"

```cmd
# Tìm process đang dùng port 8080
netstat -ano | findstr :8080

# Kill process (thay <PID> bằng số thực tế)
taskkill /PID <PID> /F
```

### ❌ Session không lưu (Recent Conversions trống)

Đã fix bằng cách đổi `sameSiteCookies="lax"` trong `context.xml`.

### ❌ Build lỗi

```cmd
# Clean và rebuild
mvn clean install

# Skip tests nếu cần
mvn clean install -DskipTests
```

### ❌ File không upload được

- Kiểm tra Cloudinary credentials trong `application.properties`
- Kiểm tra MySQL có chạy không
- Xem logs: `view_logs.bat`

---

## 📊 Database Schema

### Bảng `users`
- `user_id` - Primary key
- `username`, `email` - Thông tin user
- `password` - Hashed password
- `picture_url` - Avatar URL

### Bảng `files`
- `file_id` - UUID primary key
- `user_id` - Foreign key to users
- `original_name`, `saved_name` - File names
- `file_path` - Cloudinary URL
- `public_id` - Cloudinary public_id
- `input_format`, `output_format` - File types
- `current_status` - UPLOADED, PROCESSING, CONVERTED, FAILED

### Bảng `tasks`
- `task_id` - Primary key
- `file_id` - Foreign key to files
- `task_type` - PDF_TO_DOCX, DOCX_TO_PDF, IMAGE_TO_PDF, CSV_TO_JSON
- `status` - PENDING, PROCESSING, COMPLETED, FAILED
- `error_message` - Error details if failed

---

## 🌐 API Endpoints

| Endpoint | Method | Mô tả |
|----------|--------|-------|
| `/` | GET | Landing page |
| `/home` | GET | Dashboard với upload form |
| `/upload` | POST | Upload file và tạo conversion task |
| `/jobs` | GET | API trả về danh sách jobs (JSON) |
| `/auth` | GET/POST | Đăng nhập/đăng ký |
| `/forgotPW` | GET/POST | Quên mật khẩu |

---

## 🔐 Security

- ✅ Session cookies: `sameSiteCookies="lax"` cho HTTP localhost
- ✅ Password hashing: BCrypt
- ✅ SQL Injection protection: PreparedStatement
- ✅ File validation: Kiểm tra extension và MIME type
- ⚠️ HTTPS recommended cho production

---

## 🚀 Deploy lên Production

1. Đổi sang HTTPS
2. Set `sameSiteCookies="strict"` trong context.xml
3. Update Cloudinary credentials
4. Cấu hình MySQL với password mạnh
5. Enable Tomcat security manager
6. Set Java heap size cho Tomcat:
   ```
   CATALINA_OPTS="-Xms512m -Xmx2048m"
   ```

---

## 📝 Changelog

### Version 1.1 (Current)
- ✅ Added CSV to JSON conversion
- ✅ Fixed session cookie issues (sameSiteCookies)
- ✅ Separated test project (convertfile-core + convertfile-tests)
- ✅ Fixed resource leaks in CSV service
- ✅ Removed alert() popups from UI
- ✅ Standalone MySQL + Tomcat setup scripts

### Version 1.0
- ✅ PDF ↔ DOCX conversion
- ✅ Image to PDF conversion
- ✅ Cloudinary integration
- ✅ Background file worker
- ✅ User authentication

---

## 👨‍💻 Contributors

- **Author**: [@capkimkhanh2k5](https://github.com/capkimkhanh2k5)
- **Tech Stack**: Java 21, Jakarta EE 10, MySQL 9.5, Tomcat 10.1.49

---

## 📄 License

Educational project - No specific license.

---

## 💡 Tips

- Sử dụng `deploy_standalone.bat` cho production-like environment
- Chạy `mvn clean install` trước khi deploy nếu có lỗi
- Check logs thường xuyên: `view_logs.bat`
- Backup database trước khi update schema
- Test trên local trước khi deploy production

---

**Happy Coding! 🎉**
