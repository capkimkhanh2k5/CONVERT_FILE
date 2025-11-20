<div align="center">

# 🚀 CONVERT_FILE - Document Conversion System

### Professional Document Conversion Platform with Cloud Storage

[![Java](https://img.shields.io/badge/Java-21+-ED8B00?style=for-the-badge&logo=openjdk)](https://openjdk.java.net/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta_EE-6.0-007396?style=for-the-badge&logo=eclipse)](https://jakarta.ee/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1?style=for-the-badge&logo=mysql)](https://www.mysql.com/)
[![Cloudinary](https://img.shields.io/badge/Cloudinary-Cloud_Storage-3448C5?style=for-the-badge&logo=cloudinary)](https://cloudinary.com/)

*A scalable document processing and conversion platform built on Java EE architecture with cloud storage integration*

[Features](#-features) • [Architecture](#-architecture) • [Quick Start](#-quick-start) • [API Reference](#-api-reference) • [Documentation](#-documentation)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Architecture](#-architecture)
- [Technology Stack](#-technology-stack)
- [Quick Start](#-quick-start)
- [Configuration](#-configuration)
- [Database Schema](#-database-schema)
- [API Reference](#-api-reference)
- [Security](#-security)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🎯 Overview

**CONVERT_FILE** is a professional web-based document conversion system designed to handle and convert popular office document formats. Built on Java EE (Servlet/JSP) with MySQL and Cloudinary cloud storage, this system provides an efficient queue-based architecture for managing conversion tasks.

### Key Capabilities

- ⚡ **Multi-Format Conversion** — Supports DOCX, PDF, HTML, XML, CSV, and more
- 🔄 **Asynchronous Task System** — Queue-based task management with worker processing
- 🔐 **Secure Authentication** — Email OTP verification, password hashing (BCrypt), Google OAuth
- ☁️ **Cloud Storage** — Cloudinary integration for scalable file storage
- 💾 **Persistent Storage** — MySQL database with ACID compliance
- 📧 **Email Service** — Professional HTML emails for OTP and notifications

---

## ✨ Features

### Core Functionality

#### Document Conversion
| Conversion Type | Description | Status |
|----------------|-------------|---------|
| **DOCX → PDF** | Convert Word documents to PDF | ✅ Supported |
| **PDF → DOCX** | Convert PDF to editable Word | ✅ Supported |
| **DOCX → XML** | Export Word to XML format | ✅ Supported |
| **XML → DOCX** | Import XML to Word format | ✅ Supported |
| **DOCX → HTML** | Convert Word to HTML | ✅ Supported |
| **HTML → DOCX** | Convert HTML to Word | ✅ Supported |
| **DOCX → CSV** | Extract tables to CSV | ✅ Supported |
| **CSV → DOCX** | Import CSV as Word table | ✅ Supported |
| **DOCX Merge** | Combine multiple DOCX files | ✅ Supported |
| **PDF Merge** | Combine multiple PDF files | ✅ Supported |

### Authentication & Security

- **📧 Email OTP Verification** — Secure registration with 6-digit OTP
  - BCrypt-hashed OTP storage in session
  - 5-minute expiry with auto-cleanup
  - Rate limiting (60-second cooldown)
  - Maximum 5 verification attempts
  - Resend functionality with countdown timer
  - Privacy-protected logging (masked emails)

- **🔐 Password Management**
  - BCrypt password hashing (12 rounds)
  - Forgot password flow with OTP
  - Secure password reset

- **🌐 Google OAuth** — Sign in with Google integration

### Technical Features

- 📦 **Queue-Based Task Processing** — Database-driven task queue with worker threads
- ☁️ **Cloudinary Integration** — Cloud file storage with public URL access
- 🎨 **Server-Side Rendering** — JSP/JSTL templating engine
- 📤 **Multipart File Upload** — Robust file upload handling
- 🔄 **Centralized Services** — OTPService, EmailService, PropertiesService
- 📊 **Session Management** — Secure session handling with cleanup
- 🛡️ **SQL Injection Protection** — PreparedStatement usage throughout
- 📝 **Comprehensive Logging** — Privacy-aware logging system

---

## 🏗️ Architecture

### System Overview

```
┌───────────────────────────────────────────────────────────────┐
│                       Client Layer                            │
│                    (Web Browser)                              │
└────────────────────────────┬──────────────────────────────────┘
                             │
                ┌────────────▼──────────────┐
                │   Presentation Layer      │
                │  ┌─────────────────────┐  │
                │  │     Servlets        │  │
                │  │  - SendOTPServlet   │  │
                │  │  - VerifyOTPServlet │  │
                │  │  - UploadServlet    │  │
                │  │  - LoginServlet     │  │
                │  └──────────┬──────────┘  │
                │  ┌──────────▼──────────┐  │
                │  │        JSP          │  │
                │  │  - auth.jsp         │  │
                │  │  - home.jsp         │  │
                │  └─────────────────────┘  │
                └────────────┬──────────────┘
                             │
                ┌────────────▼──────────────┐
                │   Business Logic Layer    │
                │  ┌─────────────────────┐  │
                │  │   Core Services     │  │
                │  │  - OTPService       │  │
                │  │  - EmailService     │  │
                │  │  - FileService      │  │
                │  └──────────┬──────────┘  │
                │  ┌──────────▼──────────┐  │
                │  │   Business Objects  │  │
                │  │  - UserBO           │  │
                │  │  - FileBO           │  │
                │  └──────────┬──────────┘  │
                │  ┌──────────▼──────────┐  │
                │  │     Workers         │  │
                │  │  - FileWorker       │  │
                │  └─────────────────────┘  │
                └────────────┬──────────────┘
                             │
                ┌────────────▼──────────────┐
                │   Data Access Layer       │
                │  ┌─────────────────────┐  │
                │  │        DAOs         │  │
                │  │  - UserDAO          │  │
                │  │  - FileDAO          │  │
                │  │  - TaskQueueDAO     │  │
                │  └──────────┬──────────┘  │
                │  ┌──────────▼──────────┐  │
                │  │   MySQL Database    │  │
                │  │  (file_converter)   │  │
                │  └─────────────────────┘  │
                └───────────────────────────┘
                             │
                ┌────────────▼──────────────┐
                │   External Services       │
                │  ┌─────────────────────┐  │
                │  │    Cloudinary       │  │
                │  │  (File Storage)     │  │
                │  └─────────────────────┘  │
                │  ┌─────────────────────┐  │
                │  │    Gmail SMTP       │  │
                │  │  (Email Service)    │  │
                │  └─────────────────────┘  │
                └───────────────────────────┘
```

### Design Patterns

- **MVC (Model-View-Controller)** — Separation of concerns
- **DAO (Data Access Object)** — Database abstraction layer
- **Service Layer Pattern** — Centralized business logic
- **Producer-Consumer** — Queue-based task processing
- **Front Controller** — Servlet-based request routing
- **Singleton** — Database connection pooling

---

## 🛠️ Technology Stack

### Backend

```yaml
Core Framework:
  Java SE: 21+
  Jakarta Servlet: 6.0.0
  Jakarta JSP: 3.1.0
  Jakarta JSTL: 3.0.1

Document Processing:
  docx4j-core: 11.5.6
  docx4j-export-fo: 11.5.6
  docx4j-ImportXHTML: 11.4.8
  Apache POI: 5.4.1
  Apache PDFBox: 2.0.30
  iText PDF: 5.5.13.4

Utility Libraries:
  commons-fileupload: 1.5
  commons-io: 2.21.0
  commons-collections4: 4.5.0
  flexmark-all: 0.64.8
  commonmark: 0.27.0

Security:
  jBCrypt: 0.4
  Google OAuth: 1.39.0
  Google API Client: 2.8.1

Cloud & External Services:
  Cloudinary: 1.39.0
  JavaMail: 1.6.2

Database:
  MySQL Connector: 8.3.0

Data Processing:
  Jackson: 2.20.0
  Gson: (via Google libs)
  JSON: 20240303

Build Tool:
  Apache Maven: 3.8+
```

### Infrastructure

```yaml
Application Server:
  Apache Tomcat: 11.0+ (Jakarta EE 6.0 compatible)
  
Database:
  MySQL: 8.0+
  Engine: InnoDB
  Charset: utf8mb4_unicode_ci

Cloud Storage:
  Cloudinary: Cloud-based file storage

Email Service:
  Gmail SMTP: TLS/SSL enabled
```

---

## 🚀 Quick Start

### Prerequisites

```bash
# Required software
- JDK 21 or higher
- Apache Maven 3.8+
- MySQL 8.0+
- Apache Tomcat 11.0+ (Jakarta EE 6.0 compatible)
- Cloudinary account (for file storage)
- Gmail account with App Password (for email service)
```

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/capkimkhanh2k5/CONVERT_FILE.git
cd CONVERT_FILE
```

**2. Database Setup**

```bash
# Login to MySQL
mysql -u root -p

# Create database and tables
mysql -u root -p < src/main/java/com/convertfile/model/BD_Query.sql
```

*Note: The SQL script creates the `file_converter` database with tables `users`, `files`, and `tasks`. It also inserts sample users (`user01`, `admin`) with password `123456`.*

**3. Configuration**

Create `src/main/resources/application.properties`:

```properties
# Database Configuration
database.url=jdbc:mysql://localhost:3306/file_converter?useSSL=false&serverTimezone=UTC
database.username=root
database.password=your_mysql_password

# Email Configuration (Gmail SMTP)
email.username=your_email@gmail.com
email.password=your_gmail_app_password

# Google OAuth Configuration
google.client.id=your_google_client_id.apps.googleusercontent.com
google.client.secret=your_google_client_secret

# Cloudinary Configuration
cloudinary.cloud.name=your_cloudinary_cloud_name
cloudinary.api.key=your_cloudinary_api_key
cloudinary.api.secret=your_cloudinary_api_secret
cloudinary.url.secure=true
```

**4. Build the project**

```bash
mvn clean package
```

**5. Deploy to Tomcat**

```bash
# Copy WAR file to Tomcat webapps
cp target/CONVERT_FILE.war $TOMCAT_HOME/webapps/

# Start Tomcat
$TOMCAT_HOME/bin/startup.sh
```

**6. Access the application**

```
http://localhost:8080/CONVERT_FILE/
```

**Default test accounts:**
- Username: `user01` / Password: `123456`
- Username: `admin` / Password: `123456`

---

## ⚙️ Configuration

### Gmail App Password Setup

1. Go to Google Account Security: https://myaccount.google.com/security
2. Enable 2-Step Verification
3. Generate App Password for "Mail"
4. Use this password in `application.properties`

### Cloudinary Setup

1. Create account at: https://cloudinary.com/
2. Get Cloud Name, API Key, API Secret from Dashboard
3. Add credentials to `application.properties`

---

## 💾 Database Schema

### Entity Relationship Diagram

```
┌──────────────────────┐
│       users          │
├──────────────────────┤
│ user_id (PK)         │
│ username (UNIQUE)    │
│ password (CHAR(60))  │ ← BCrypt hash
│ email (UNIQUE)       │
│ created_at           │
└──────────┬───────────┘
           │ 1:N
           │
┌──────────▼───────────┐
│        files         │
├──────────────────────┤
│ file_id (PK, UUID)   │
│ user_id (FK)         │
│ original_name        │
│ saved_name (UNIQUE)  │
│ file_type            │
│ file_size            │
│ storage_type         │ ← 'cloudinary' or 'local'
│ cloudinary_url       │
│ current_status       │
│ upload_date          │
└──────────┬───────────┘
           │ 1:N
           │
┌──────────▼───────────┐
│        tasks         │
├──────────────────────┤
│ task_id (PK)         │
│ file_id (FK)         │
│ task_type (ENUM)     │ ← e.g., 'DOCX_TO_PDF'
│ status (ENUM)        │ ← 'WAITING', 'PROCESSING', etc.
│ input_path           │
│ output_path          │
│ cloudinary_output    │
│ created_at           │
│ updated_at           │
│ message              │
└──────────────────────┘
```

### Task Status Flow

```
WAIT ING (Pending)
     │
     ▼
PROCESSING (In Progress) ──┐
     │                     │
     ▼                     ▼
COMPLETED              FAILED
     │                     │
     └─────────────────────┴──────▶ CANCELED (Optional)
```

---

## 📡 API Reference

### Authentication Endpoints

| Endpoint | Method | Description | Request Body |
|----------|--------|-------------|--------------|
| `/register` | POST | Register new user | `username`, `email`, `password`, `confirm` |
| `/send-otp` | POST | Send OTP to email | `email` |
| `/verify-otp` | POST | Verify OTP code | `otp` |
| `/login` | POST | User login | `username`, `password` |
| `/forgot-password` | POST | Request password reset | `action=sendCode`, `email` |
| `/forgot-password` | POST | Verify reset OTP | `action=verifyCode`, `code` |
| `/forgot-password` | POST | Reset password | `action=resetPassword`, `newPassword` |
| `/logout` | GET | User logout | - |

### File Operations

| Endpoint | Method | Description | Content-Type |
|----------|--------|-------------|--------------|
| `/upload` | POST | Upload file for conversion | `multipart/form-data` |
| `/download` | GET | Download converted file | - |
| `/home` | GET | View conversion history | - |

### OTP Verification Flow

```javascript
// 1. Send OTP
POST /send-otp
Content-Type: application/x-www-form-urlencoded
email=user@example.com

Response: {
  "success": true,
  "message": "OTP sent successfully",
  "expiryMinutes": 5
}

// 2. Verify OTP
POST /verify-otp
Content-Type: application/x-www-form-urlencoded
otp=123456

Response: {
  "success": true,
  "message": "OTP verified successfully"
}
```

### Upload Example

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

## 🔒 Security

### Implemented Security Measures

✅ **Password Security**
- BCrypt hashing with 12 rounds
- No plain-text password storage

✅ **OTP Security**
- BCrypt-hashed OTP in session
- 5-minute expiry with auto-cleanup
- Rate limiting (60s cooldown)
- Maximum 5 verification attempts
- Session-based storage (not exposed to client)

✅ **Session Management**
- HttpSession with secure attributes
- Session timeout configuration
- Automatic cleanup on logout

✅ **Input Validation**
- Email format validation (regex)
- File size limits (configurable)
- File type verification
- OTP format validation (6 digits)

✅ **SQL Injection Protection**
- PreparedStatement throughout DAOs
- Parameterized queries only

✅ **Privacy Protection**
- Email masking in logs (`cap***@gmail.com`)
- No PII in server logs

✅ **Path Traversal Prevention**
- UUID-based file naming
- Sanitized file paths

### Configuration Security

**File Upload Limits** (`web.xml`):
```xml
<multi part-config>
    <max-file-size>52428800</max-file-size>      <!-- 50MB -->
    <max-request-size>104857600</max-request-size> <!-- 100MB -->
</multipart-config>
```

**Session Timeout**:
```xml
<session-config>
    <session-timeout>30</session-timeout> <!-- 30 minutes -->
</session-config>
```

---

## 📊 Project Structure

```
CONVERT_FILE/
├── src/main/
│   ├── java/com/convertfile/
│   │   ├── controller/              # Servlets (Controllers)
│   │   │   ├── SendOTPServlet.java
│   │   │   ├── VerifyOTPServlet.java
│   │   │   ├── LoginServlet.java
│   │   │   ├── RegisterServlet.java
│   │   │   ├── UploadServlet.java
│   │   │   ├── DownloadServlet.java
│   │   │   ├── ForgotPWServlet.java
│   │   │   ├── GoogleLoginServlet.java
│   │   │   └── LogOutServlet.java
│   │   │
│   │   ├── service/                 # Business Logic Services
│   │   │   ├── OTPService.java      # Centralized OTP management
│   │   │   ├── EmailService.java    # Email sending service
│   │   │   ├── PropertiesService.java # Config management
│   │   │   ├── passwordService.java
│   │   │   └── FileService.java
│   │   │
│   │   ├── bo/                      # Business Objects  
│   │   │   ├── UserBO.java
│   │   │   └── FileBO.java
│   │   │
│   │   ├── dao/                     # Data Access Objects
│   │   │   ├── ConnectDB.java
│   │   │   ├── UserDAO.java
│   │   │   ├── FileDAO.java
│   │   │   └── TaskQueueDAO.java
│   │   │
│   │   ├── model/                   # Entities
│   │   │   ├── User.java
│   │   │   ├── FileInfo.java
│   │   │   ├── TaskJob.java
│   │   │   └── BD_Query.sql
│   │   │
│   │   └── worker/
│   │       └── FileWorker.java      # Background task processor
│   │
│   ├── webapp/
│   │   ├── WEB-INF/
│   │   │   └── web.xml              # Deployment descriptor
│   │   ├── auth.jsp                 # Login/Register page
│   │   ├── home.jsp                 # Dashboard
│   │   ├── forgotPW.jsp             # Password reset
│   │   └── assets/                  # CSS, JS, images
│   │
│   └── resources/
│       └── application.properties   # App configuration
│
├── pom.xml                          # Maven configuration
├── deploy.sh                        # Deployment script
└── README.md
```

---

## 🧪 Testing

### Manual Testing Checklist

**Registration Flow:**
- [ ] Register with valid email → OTP sent
- [ ] Enter correct OTP → Email verified
- [ ] Complete registration → Account created
- [ ] Try duplicate email → Error shown

**OTP Verification:**
- [ ] Request OTP twice within 60s → Rate limit error
- [ ] Enter wrong OTP 5 times → Max attempts error
- [ ] Wait 5+ minutes → OTP expired error
- [ ] Click Resend → New OTP sent, countdown restarted

**File Conversion:**
- [ ] Upload DOCX → Conversion starts
- [ ] View progress in dashboard
- [ ] Download converted file
- [ ] Verify file quality

**Google OAuth:**
- [ ] Sign in with Google → Account created/logged in
- [ ] Check session management
- [ ] Logout properly

---

## 🎯 Performance Considerations

- **Queue-Based Processing** — Prevents server overload
- **Cloud Storage** — Scalable file storage with Cloudinary
- **Connection Pooling** — Efficient database connections
- **Session Management** — Automatic cleanup of expired sessions
- **Worker Architecture** — Background task processing doesn't block UI

---

## 🐛 Troubleshooting

### Common Issues

**Issue: OTP not received**
- Check email.username and email.password in properties
- Verify Gmail App Password is correct
- Check spam folder

**Issue: File upload fails**
- Check Cloudinary credentials
- Verify file size limits in web.xml
- Check disk space if using local storage

**Issue: Database connection errors**
- Verify MySQL is running
- Check database.url, username, password
- Ensure database schema is created

---

## 👥 Contributing

We welcome contributions! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/NewFeature`)
3. Commit changes (`git commit -m 'Add NewFeature'`)
4. Push to branch (`git push origin feature/NewFeature`)
5. Open a Pull Request

### Code Style
- Follow Java naming conventions
- Add JavaDoc comments for public methods
- Use meaningful variable names
- Write clean, readable code

---

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- [docx4j](https://www.docx4java.org/) — Core document processing library
- [Apache Commons](https://commons.apache.org/) — Utility libraries
- [Cloudinary](https://cloudinary.com/) — Cloud file storage
- [MySQL](https://www.mysql.com/) — Database management system
- [Google](https://developers.google.com/) — OAuth integration
- Da Nang University of Science and Technology

---

## 📞 Contact & Support

- **University**: Da Nang University of Science and Technology
- **Project**: Network Programming Assignment
- **GitHub**: [capkimkhanh2k5/CONVERT_FILE](https://github.com/capkimkhanh2k5/CONVERT_FILE)

---

<div align="center">

**Built with ❤️ using Java EE & Modern Web Technologies**

**Version**: 1.0-SNAPSHOT  
**Last Updated**: November 2025

[⬆ Back to top](#-convert_file---document-conversion-system)

</div>
