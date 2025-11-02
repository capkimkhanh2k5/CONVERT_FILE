<div align="center">

# 🚀 CONVERT_FILE - Hệ thống Chuyển đổi Tập tin

### Hệ thống Chuyển đổi Tài liệu JSP/Servlet

[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://openjdk.java.net/)
[![Servlet](https://img.shields.io/badge/Servlet-6.0-blue.svg)](https://jakarta.ee/specifications/servlet/6.0/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

*Một nền tảng xử lý và chuyển đổi tài liệu có khả năng mở rộng được xây dựng trên kiến trúc Java EE*

[Tính năng](#-tính-năng) • [Kiến trúc](#-kiến-trúc) • [Bắt đầu nhanh](#-bắt-đầu-nhanh) • [Tài liệu](#-tài-liệu)

</div>

---

## 📋 Mục lục

- [Tổng quan](#-tổng-quan)
- [Tính năng](#-tính-năng)
- [Kiến trúc](#-kiến-trúc)
- [Công nghệ](#-công-nghệ)
- [Bắt đầu nhanh](#-bắt-đầu-nhanh)
- [Thiết kế Hệ thống](#-thiết-kế-hệ-thống)
- [Tham khảo API](#-tham-khảo-api)
- [Bảo mật](#-bảo-mật)
- [Cấu trúc Dự án](#-cấu-trúc-dự-án)
- [Đóng góp](#-đóng-góp)

---

## 🎯 Tổng quan

**CONVERT_FILE** là một hệ thống chuyển đổi tập tin dựa trên web, được thiết kế để xử lý và chuyển đổi các định dạng tài liệu văn phòng phổ biến. Được xây dựng trên nền tảng Java EE (Servlet/JSP) và MySQL, hệ thống này cung cấp một kiến trúc dựa trên hàng đợi (queue) để quản lý các tác vụ chuyển đổi một cách hiệu quả.

### Các khả năng chính

- ⚡ **Chuyển đổi Đa định dạng** — Hỗ trợ các định dạng DOCX, PDF, HTML, và XML.
- 🔄 **Hệ thống Tác vụ Bất đồng bộ** — Quản lý các yêu cầu chuyển đổi thông qua một hàng đợi tác vụ (task queue) trong cơ sở dữ liệu.
- 🔐 **Quản lý Người dùng** — Hệ thống đăng ký và đăng nhập an toàn sử dụng băm mật khẩu jbcrypt.
- 💾 **Lưu trữ Bền bỉ** — Quản lý trạng thái tập tin, người dùng và tác vụ bằng MySQL.

---

## ✨ Tính năng

### Chức năng Cốt lõi

Hệ thống hỗ trợ nhiều tác vụ chuyển đổi, được định nghĩa trong cơ sở dữ liệu.

| Tính năng | Mô tả | Trạng thái (Bảng `tasks` - `task_type`) |
|---|---|---|
| **DOCX → PDF** | Chuyển đổi file DOCX sang PDF | ✅ `DOCX_TO_PDF` |
| **PDF → DOCX** | Chuyển đổi file PDF sang DOCX | ✅ `PDF_TO_DOCX` |
| **DOCX → XML** | Chuyển đổi file DOCX sang XML | ✅ `DOCX_TO_XML` |
| **XML → DOCX** | Chuyển đổi file XML sang DOCX | ✅ `XML_TO_DOCX` |
| **DOCX → HTML** | Chuyển đổi file DOCX sang HTML | ✅ `DOCX_TO_HTML` |
| **DOCX Merge** | Gộp nhiều file DOCX | ✅ `DOCX_MERGE` |

### Tính năng Kỹ thuật

- 🔐 **Hệ thống Xác thực** — Băm mật khẩu (jbcrypt) và quản lý phiên (session).
- 📦 **Hệ thống Hàng đợi Tác vụ** — Dựa trên bảng `tasks` của CSDL, được xử lý bởi các `worker`.
- 💾 **Lưu trữ Quan hệ** — Thiết kế CSDL với MySQL (InnoDB) hỗ trợ ACID.
- 🎨 **Giao diện Người dùng** — Giao diện web được render phía máy chủ (SSR) bằng JSP/JSTL.
- 📤 **Upload File** — Xử lý upload file đa-phần (multipart) với `commons-fileupload`.

---

## 🏗️ Kiến trúc

### Tổng quan Hệ thống

Kiến trúc hệ thống tuân theo mô hình Layered Architecture và MVC, phù hợp với các ứng dụng Servlet/JSP.

```
┌─────────────────────────────────────────────────────────────────┐
│                        Lớp Client                               │
│                      (Trình duyệt web)                          │
└────────────────────────────────┬────────────────────────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │    Lớp Trình diễn       │
                    │    (Presentation)       │
                    │   ┌──────────────────┐  │
                    │   │     Servlets     │  │
                    │   │ (Upload/Login...)│  │
                    │   └────────┬─────────┘  │
                    │   ┌────────▼─────────┐  │
                    │   │       JSP        │  │
                    │   │      (Views)     │  │
                    │   └──────────────────┘  │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Lớp Nghiệp vụ Business │
                    │   ┌──────────────────┐  │
                    │   │ FileService      │  │
                    │   │ TaskQueueService │  │
                    │   └────────┬─────────┘  │
                    │   ┌────────▼─────────┐  │
                    │   │   fileWorker     │  │
                    │   │ (Xử lý tác vụ)   │  │
                    │   └──────────────────┘  │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │ Lớp Truy cập Dữ liệu DAO│
                    │   ┌──────────────────┐  │
                    │   │ UserDAO, FileDAO │  │
                    │   │  TaskQueueDAO    │  │
                    │   └────────┬─────────┘  │
                    │   ┌────────▼─────────┐  │
                    │   │  MySQL Database  │  │
                    │   │ (file_converter) │  │
                    │   └──────────────────┘  │
                    └─────────────────────────┘
```

### Các Mẫu thiết kế (Patterns)

- **MVC (Model-View-Controller)** — Tách biệt logic nghiệp vụ, hiển thị và điều khiển.
- **DAO (Data Access Object)** — Trừu tượng hóa các hoạt động truy cập cơ sở dữ liệu.
- **Producer-Consumer** — `UploadServlet` (Producer) tạo tác vụ, `fileWorker` (Consumer) xử lý tác vụ.
- **Front Controller** — Các Servlet (`UploadServlet`, `LoginServlet`) đóng vai trò là điểm vào trung tâm.

---

## 🛠️ Công nghệ

### Backend
```yaml
Core:
  - Java SE: 21+
  - Jakarta Servlet: 6.0
  - Jakarta JSP/JSTL: 3.1/3.0

Thư viện chính:
  - org.docx4j: 11.5.6      # Xử lý DOCX, PDF, HTML
  - commons-fileupload: 1.5  # Xử lý upload file
  - commons-io: 2.15.1       # Thao tác file
  - mysql-connector-j: 8.0.33 # Trình điều khiển JDBC
  - org.mindrot.jbcrypt: 0.4 # Băm mật khẩu
  - org.slf4j: 2.0.13        # Logging
  - org.apache.xmlgraphics: fop: 2.8 # Tạo PDF từ FO

Build:
  - Apache Maven: 3.8+
```

### Infrastructure

```yaml
Server:
  - Apache Tomcat: 9.0+ (Hoặc bất kỳ Servlet Container 6.0 nào)

Database:
  - MySQL: 8.0+
  - InnoDB Engine
  - Mã hóa UTF-8mb4
```

---

## 🚀 Bắt đầu nhanh

### Điều kiện tiên quyết

```bash
# Phần mềm yêu cầu
- JDK 21 hoặc cao hơn
- Apache Maven 3.8+
- MySQL 8.0+
- Apache Tomcat 9.0+ (hoặc tương đương)
```

### Cài đặt

1. **Clone dự án**

```bash
git clone [URL_DỰ_ÁN_CỦA_BẠN]
cd CONVERT_FILE
```

2. **Cấu hình cơ sở dữ liệu**

```bash
# Đăng nhập vào MySQL
mysql -u root -p

# Chạy script để tạo database, tables, và user mẫu
# (Đảm bảo script BD_Query.sql không có lệnh DROP ở cuối khi chạy lần đầu)
mysql -u root -p < src/main/java/com/convertfile/model/BD_Query.sql
```

*Lưu ý: Script `BD_Query.sql` tạo CSDL `file_converter` và các bảng `users`, `files`, `tasks`. Nó cũng chèn 2 người dùng mẫu (`user01`, `admin`) với mật khẩu là `123456`.*

3. **Cấu hình ứng dụng**

```bash
# Cập nhật thông tin kết nối CSDL trong
# src/main/java/com/convertfile/dao/ConnectDB.java
# (Hiện tại có thể đang hard-code, cần kiểm tra lại)
```

4. **Build dự án**

```bash
mvn clean package
```

5. **Deploy lên Tomcat**

```bash
# Sao chép file .war đã build vào thư mục webapps của Tomcat
cp target/CONVERT_FILE.war $TOMCAT_HOME/webapps/

# Khởi động Tomcat
$TOMCAT_HOME/bin/startup.sh
```

6. **Truy cập ứng dụng**

```
http://localhost:8080/CONVERT_FILE/
(Trang mặc định là upload.jsp)
```

---

## 🔬 Thiết kế Hệ thống

### Lược đồ CSDL

Lược đồ CSDL được thiết kế để quản lý người dùng, các tệp tin đã tải lên, và các tác vụ chuyển đổi liên quan.

```
┌─────────────────────┐
│       users         │
├─────────────────────┤
│ user_id (PK)        │
│ username (UNIQUE)   │
│ password (CHAR(60)) │
│ email (UNIQUE)      │
└──────────┬──────────┘
           │
           │ 1:N (Hoặc Null)
           │
┌──────────▼──────────┐
│        files        │
├─────────────────────┤
│ file_id (PK, UUID)  │
│ user_id (FK)        │
│ original_name       │
│ saved_name (UNIQUE) │
│ current_status      │◄──┐
└──────────┬──────────┘    │
           │               │
           │ 1:N           │
           │               │
┌──────────▼──────────┐    │
│        tasks        │    │
├─────────────────────┤    │
│ task_id (PK)        │    │
│ file_id (FK)        │────┘
│ task_type (ENUM)    │
│ status (ENUM)       │
│ message             │
└─────────────────────┘
```

### Luồng Trạng thái Tác vụ (`tasks.status`)

```
    WAITING (Đang chờ)
       │
       ▼
   PROCESSING (Đang xử lý) ───┐
       │                      │
       ▼                      ▼
   COMPLETED (Hoàn thành)   FAILED (Thất bại)
                              │
                              ▼
                          CANCELED (Bị hủy)
```

### Luồng xử lý của Worker

```
┌─────────────────────────────────────────────┐
│                                             │
│  while (running) {                          │
│    1. Lấy tác vụ (task) từ CSDL (status='WAITING')│
│    2. Cập nhật status → 'PROCESSING'          │
│    3. Thực thi tác vụ (dùng docx4j):          │
│       • Tải file từ input_path               │
│       • Chuyển đổi...                        │
│       • Ghi file vào output_path             │
│    4. Cập nhật CSDL:                         │
│       • Cập nhật status → 'COMPLETED'/'FAILED'│
│       • Cập nhật files.output_path           │
│  }                                          │
└─────────────────────────────────────────────┘
```

---

## 📡 Tham khảo API

Các endpoint được xử lý bởi các Servlet.

| Endpoint (Dự đoán) | Method | Class | Mô tả |
|---|---|---|---|
| `/login` | POST | `LoginServlet` | Xác thực thông tin đăng nhập của người dùng |
| `/register` | POST | `RegisterServlet` | Đăng ký tài khoản người dùng mới |
| `/logout` | GET | `LogOutServlet` | Đăng xuất và hủy phiên làm việc |
| `/upload` | POST | `UploadServlet` | Tải file lên và tạo một tác vụ chuyển đổi mới |

### Ví dụ Request Upload

`UploadServlet` xử lý `multipart/form-data`, được gửi từ `upload.jsp`.

```http
POST /CONVERT_FILE/upload HTTP/1.1
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary

------WebKitFormBoundary
Content-Disposition: form-data; name="file"; filename="document.docx"
Content-Type: application/vnd.openxmlformats-officedocument.wordprocessingml.document

[binary data]
------WebKitFormBoundary
Content-Disposition: form-data; name="taskType"

DOCX_TO_PDF
------WebKitFormBoundary--
```

---

## 🔒 Bảo mật

### Các biện pháp đã triển khai

- ✅ **Bảo mật Mật khẩu** — Sử dụng **jBCrypt** để băm và xác minh mật khẩu (thay vì SHA-256).
- ✅ **Quản lý Phiên (Session)** — Sử dụng `HttpSession` của Servlet.
- ✅ **Ngăn chặn Path Traversal** — Cần được thực hiện trong `FileService` khi xử lý tên tệp và đường dẫn.
- ✅ **Xác thực Đầu vào** — Giới hạn kích thước tệp và loại tệp (được cấu hình trong `web.xml`).
- ✅ **Bảo vệ Chống SQL Injection** — Sử dụng `PreparedStatement` trong các lớp DAO.

### Cấu hình Bảo mật (`web.xml`)

Cấu hình `multipart-config` giúp giới hạn tài nguyên và ngăn chặn các cuộc tấn công DoS cơ bản qua việc upload file.

```xml
<servlet>
    <servlet-name>UploadServlet</servlet-name>
    <servlet-class>com.convertfile.controller.UploadServlet</servlet-class>
    <multipart-config>
        <location>${catalina.base}/temp</location>
        <max-file-size>52428800</max-file-size>
        <max-request-size>104857600</max-request-size>
        <file-size-threshold>1048576</file-size-threshold>
    </multipart-config>
</servlet>
```

---

## 📊 Cấu trúc Dự án

```
CONVERT_FILE/
├── src/main/
│   ├── java/com/convertfile/
│   │   ├── controller/          # Lớp Servlet (Controllers)
│   │   │   ├── LoginServlet.java
│   │   │   ├── RegisterServlet.java
│   │   │   ├── UploadServlet.java
│   │   │   └── LogOutServlet.java
│   │   ├── dao/                 # Data Access Objects
│   │   │   ├── ConnectDB.java
│   │   │   ├── UserDAO.java
│   │   │   ├── FileDAO.java
│   │   │   └── TaskQueueDAO.java
│   │   ├── model/               # Các đối tượng (Entities)
│   │   │   ├── User.java
│   │   │   ├── FileInfo.java
│   │   │   ├── TaskJob.java
│   │   │   └── BD_Query.sql
│   │   ├── service/             # Logic nghiệp vụ (Business Logic)
│   │   │   ├── FileService.java
│   │   │   ├── TaskQueueService.java
│   │   │   ├── passwordService.java
│   │   │   └── microService/   # Các dịch vụ chuyển đổi cụ thể
│   │   │       └── (docx_to_pdf_service, etc...).java
│   │   └── worker/
│   │       └── fileWorker.java   # Worker xử lý hàng đợi
│   ├── webapp/
│   │   ├── WEB-INF/
│   │   │   └── web.xml          # Bộ mô tả triển khai
│   │   ├── login.jsp
│   │   ├── register.jsp
│   │   ├── upload.jsp
│   │   └── resources/ (inputSRC, outputSRC - theo web.xml)
├── pom.xml                   # Cấu hình Maven
└── README.md
```

---

## 👥 Đóng góp

Chúng tôi hoan nghênh các đóng góp! Vui lòng tuân theo các nguyên tắc sau:

1. Fork dự án
2. Tạo một nhánh tính năng (`git checkout -b feature/TinhNangMoi`)
3. Commit các thay đổi (`git commit -m 'Thêm TinhNangMoi'`)
4. Push lên nhánh (`git push origin feature/TinhNangMoi`)
5. Mở một Pull Request

---

## 📄 Giấy phép

Dự án này được cấp phép theo Giấy phép MIT - xem tệp [LICENSE](LICENSE) để biết chi tiết.

---

## 🙏 Lời cảm ơn

- [docx4j](https://www.docx4java.org/trac/docx4j) — Thư viện xử lý tài liệu cốt lõi.
- [Apache Commons](https://commons.apache.org/) — Cung cấp các thư viện tiện ích (FileUpload, IO).
- [MySQL](https://www.mysql.com/) — Hệ quản trị CSDL.
- Đại học Bách khoa, Đại học Đà Nẵng.

---

<div align="center">

**Xây dựng bằng ❤️ với Java EE**

[⬆ Quay lại đầu trang](#-convert_file---hệ-thống-chuyển-đổi-tập-tin)
