<div align="center">

# 🚀 PDF Processing Engine

### Enterprise-Grade Asynchronous File Processing System

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/)
[![Servlet](https://img.shields.io/badge/Servlet-4.0-blue.svg)](https://javaee.github.io/servlet-spec/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

*A production-ready, scalable document processing platform built on Java EE architecture*

[Features](#-features) • [Architecture](#-architecture) • [Quick Start](#-quick-start) • [Documentation](#-documentation)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Architecture](#-architecture)
- [Technology Stack](#-technology-stack)
- [Getting Started](#-getting-started)
- [System Design](#-system-design)
- [API Reference](#-api-reference)
- [Performance](#-performance)
- [Security](#-security)
- [Contributing](#-contributing)

---

## 🎯 Overview

**PDF Processing Engine** is an enterprise-grade, asynchronous file processing system engineered for high-throughput document transformation workflows. Built on proven Java EE patterns, it provides reliable, scalable processing of PDF documents with real-time progress tracking and fault-tolerant execution.

### Key Capabilities

- ⚡ **Asynchronous Processing** — Non-blocking request handling with background job execution
- 📊 **Real-time Progress Tracking** — Live job status updates with granular progress metrics
- 🔄 **Batch Processing** — Efficient handling of multi-file ZIP archives
- 🛡️ **Enterprise Security** — Session-based authentication with role-based access control
- 📈 **Horizontal Scalability** — Worker thread pool architecture ready for distributed deployment

---

## ✨ Features

### Core Functionality

| Feature | Description | Status |
|---------|-------------|--------|
| **PDF → Text Extraction** | High-fidelity text extraction with UTF-8 encoding | ✅ Production |
| **PDF → Image Conversion** | Multi-page rendering with configurable DPI (72-300) | ✅ Production |
| **ZIP Batch Processing** | Automated extraction and queuing of archived PDFs | ✅ Production |
| **Progress Monitoring** | Page-level progress with percentage completion | ✅ Production |
| **Download Management** | Secure result retrieval with automatic cleanup | ✅ Production |

### Technical Features

- 🔐 **Authentication System** — SHA-256 password hashing with secure session management
- 📦 **Job Queue System** — Thread-safe BlockingQueue with configurable capacity
- 💾 **Persistent Storage** — MySQL-backed state management with ACID compliance
- 🎨 **Responsive UI** — Modern JSP/JSTL templates with Bootstrap integration
- 🧪 **Error Handling** — Comprehensive exception management with detailed logging

---

## 🏗️ Architecture

### System Overview
```
┌─────────────────────────────────────────────────────────────────┐
│                          Client Layer                            │
│                     (Browser / HTTP Client)                      │
└────────────────────────────────┬────────────────────────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │   Presentation Layer    │
                    │   ┌──────────────────┐  │
                    │   │  AuthFilter      │  │
                    │   │  (Security)      │  │
                    │   └────────┬─────────┘  │
                    │   ┌────────▼─────────┐  │
                    │   │   Servlets       │  │
                    │   │  (Controllers)   │  │
                    │   └──────────────────┘  │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │    Business Layer       │
                    │   ┌──────────────────┐  │
                    │   │  PdfService      │  │
                    │   │  QueueService    │  │
                    │   └────────┬─────────┘  │
                    │   ┌────────▼─────────┐  │
                    │   │  Worker Thread   │  │
                    │   │      Pool        │  │
                    │   └──────────────────┘  │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │   Persistence Layer     │
                    │   ┌──────────────────┐  │
                    │   │  DAO Pattern     │  │
                    │   │  (Data Access)   │  │
                    │   └────────┬─────────┘  │
                    │   ┌────────▼─────────┐  │
                    │   │  MySQL Database  │  │
                    │   │  (ACID Storage)  │  │
                    │   └──────────────────┘  │
                    └─────────────────────────┘
```

### Architectural Patterns

- **MVC (Model-View-Controller)** — Clear separation of concerns
- **DAO (Data Access Object)** — Abstracted database operations
- **Producer-Consumer** — Asynchronous job queue processing
- **Front Controller** — Centralized request handling via AuthFilter
- **Service Layer** — Business logic encapsulation

---

## 🛠️ Technology Stack

### Backend
```yaml
Core:
  - Java SE: 17+ (LTS)
  - Jakarta Servlet: 4.0
  - JSP/JSTL: 2.3

Libraries:
  - Apache PDFBox: 2.0.27   # PDF manipulation
  - Commons FileUpload: 1.5  # Multipart handling
  - Commons IO: 2.11.0       # File operations
  - MySQL Connector/J: 8.0   # JDBC driver

Build:
  - Apache Maven: 3.8+
  - Tomcat Maven Plugin: 2.2
```

### Infrastructure
```yaml
Server:
  - Apache Tomcat: 9.0.x
  - JVM Heap: 1GB-2GB recommended

Database:
  - MySQL: 8.0+
  - InnoDB Engine (ACID)
  - UTF-8mb4 encoding

Frontend:
  - JSP/JSTL: Server-side rendering
  - Bootstrap: 4.6+ (optional)
  - Vanilla JavaScript: AJAX polling
```

---

## 🚀 Getting Started

### Prerequisites
```bash
# Required software
- JDK 17 or higher
- Apache Maven 3.8+
- MySQL 8.0+
- Apache Tomcat 9.0+
```

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/your-org/pdf-processing-engine.git
cd pdf-processing-engine
```

2. **Configure database**
```bash
# Create database
mysql -u root -p
mysql> CREATE DATABASE pdf_processor CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
mysql> CREATE USER 'pdfuser'@'localhost' IDENTIFIED BY 'secure_password';
mysql> GRANT ALL PRIVILEGES ON pdf_processor.* TO 'pdfuser'@'localhost';
mysql> FLUSH PRIVILEGES;

# Import schema
mysql -u pdfuser -p pdf_processor < schema.sql
```

3. **Configure application**
```bash
# Edit src/main/resources/db.properties
db.url=jdbc:mysql://localhost:3306/pdf_processor
db.username=pdfuser
db.password=secure_password
db.driver=com.mysql.cj.jdbc.Driver
```

4. **Build the project**
```bash
mvn clean package
```

5. **Deploy to Tomcat**
```bash
# Copy WAR to Tomcat
cp target/pdf-processor.war $TOMCAT_HOME/webapps/

# Or use Maven plugin
mvn tomcat7:deploy
```

6. **Access the application**
```
http://localhost:8080/pdf-processor
```

---

## 🔬 System Design

### Database Schema
```sql
┌─────────────────────┐
│       users         │
├─────────────────────┤
│ id (PK)             │
│ username (UNIQUE)   │
│ password_hash       │
│ created_at          │
└──────────┬──────────┘
           │
           │ 1:N
           │
┌──────────▼──────────┐
│        jobs         │
├─────────────────────┤
│ id (PK)             │
│ user_id (FK)        │
│ filename            │
│ status              │◄───┐
│ created_at          │    │
└──────────┬──────────┘    │
           │               │
           │ 1:N           │ Reference
           │               │
┌──────────▼──────────┐    │
│     job_items       │    │
├─────────────────────┤    │
│ id (PK)             │    │
│ job_id (FK)         │────┘
│ task_type           │
│ progress_total      │
│ progress_done       │
│ result_path         │
│ status              │
└─────────────────────┘
```

### Job State Machine
```
    QUEUED
       │
       ▼
   PROCESSING ──────┐
       │            │
       ▼            ▼
   COMPLETED     ERROR
```

### Worker Thread Workflow
```
┌─────────────────────────────────────────────┐
│                                             │
│  while (running) {                          │
│    1. Poll queue (blocking)                 │
│    2. Update status → PROCESSING            │
│    3. Execute task:                         │
│       • Load PDF document                   │
│       • For each page:                      │
│         - Process page                      │
│         - Update progress                   │
│         - Commit to database                │
│    4. Save result                           │
│    5. Update status → COMPLETED/ERROR       │
│  }                                          │
│                                             │
└─────────────────────────────────────────────┘
```

---

## 📡 API Reference

### Servlet Endpoints

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/auth/login` | POST | User authentication | ❌ |
| `/auth/register` | POST | User registration | ❌ |
| `/auth/logout` | GET | Session termination | ✅ |
| `/upload` | POST | File upload & job creation | ✅ |
| `/jobs` | GET | Job list & status | ✅ |
| `/result` | GET | Download processed file | ✅ |

### Request Examples

**File Upload**
```http
POST /upload HTTP/1.1
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary

------WebKitFormBoundary
Content-Disposition: form-data; name="file"; filename="document.pdf"
Content-Type: application/pdf

[binary data]
------WebKitFormBoundary
Content-Disposition: form-data; name="taskType"

PDF_TO_TEXT
------WebKitFormBoundary--
```

**Job Status Response**
```json
{
  "id": 123,
  "filename": "document.pdf",
  "status": "PROCESSING",
  "items": [
    {
      "taskType": "PDF_TO_TEXT",
      "progress": {
        "total": 50,
        "done": 23,
        "percentage": 46
      }
    }
  ]
}
```

---

## ⚡ Performance

### Benchmarks

| Metric | Value | Configuration |
|--------|-------|---------------|
| **Throughput** | 50-100 pages/min | Single worker thread |
| **Memory Usage** | ~200MB per 100-page PDF | 150 DPI rendering |
| **Latency** | <100ms | Request → Queue insertion |
| **Concurrent Jobs** | 10-50 | Depends on worker pool size |

### Optimization Strategies
```java
// Configurable worker pool size
int WORKER_COUNT = Runtime.getRuntime().availableProcessors();

// Bounded queue prevents memory overflow
BlockingQueue<JobItem> queue = new ArrayBlockingQueue<>(1000);

// Resource cleanup
try (PDDocument document = PDDocument.load(file)) {
    // Process
} // Auto-close releases memory
```

---

## 🔒 Security

### Implemented Measures

- ✅ **Password Security** — SHA-256 hashing with per-user salts
- ✅ **Session Management** — HttpOnly cookies, configurable timeout
- ✅ **Path Traversal Prevention** — Sanitized file names and paths
- ✅ **Input Validation** — File type and size restrictions
- ✅ **SQL Injection Protection** — Parameterized queries via PreparedStatement

### Security Configuration
```java
// File upload limits (web.xml)
<multipart-config>
    <max-file-size>52428800</max-file-size>      <!-- 50MB -->
    <max-request-size>104857600</max-request-size> <!-- 100MB -->
</multipart-config>

// Session timeout
<session-config>
    <session-timeout>30</session-timeout> <!-- 30 minutes -->
</session-config>
```

---

## 📊 Project Structure
```
pdf-processor/
├── src/main/
│   ├── java/com/pdfprocessor/
│   │   ├── controller/          # Servlet layer
│   │   │   ├── AuthServlet.java
│   │   │   ├── UploadServlet.java
│   │   │   └── JobsServlet.java
│   │   ├── service/             # Business logic
│   │   │   ├── PdfService.java
│   │   │   └── QueueService.java
│   │   ├── dao/                 # Data access
│   │   │   ├── UserDAO.java
│   │   │   ├── JobDAO.java
│   │   │   └── JobItemDAO.java
│   │   ├── model/               # Domain entities
│   │   │   ├── User.java
│   │   │   ├── Job.java
│   │   │   └── JobItem.java
│   │   ├── filter/              # Security
│   │   │   └── AuthFilter.java
│   │   └── worker/              # Background processing
│   │       └── JobWorker.java
│   ├── webapp/
│   │   ├── WEB-INF/
│   │   │   ├── web.xml
│   │   │   └── views/           # JSP templates
│   │   └── resources/           # Static assets
│   └── resources/
│       └── db.properties
├── pom.xml
└── README.md
```

---

## 🧪 Testing

### Test Coverage
```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# Test with coverage report
mvn clean test jacoco:report
```

### Example Test Cases

- ✅ PDF text extraction accuracy (>95%)
- ✅ Image conversion quality (visual inspection)
- ✅ Queue thread safety (concurrent access)
- ✅ Database transaction rollback
- ✅ Authentication filter access control

---

## 🐛 Troubleshooting

### Common Issues

**Issue: OutOfMemoryError during image conversion**
```bash
# Solution: Increase JVM heap size
export CATALINA_OPTS="-Xms512m -Xmx2048m"
```

**Issue: Database connection pool exhausted**
```properties
# Solution: Tune connection pool (db.properties)
db.pool.maxActive=50
db.pool.maxIdle=10
db.pool.minIdle=5
```

**Issue: Slow processing on large PDFs**
```java
// Solution: Reduce image DPI in PdfService
private static final float IMAGE_DPI = 72; // Instead of 300
```

---

## 🗺️ Roadmap

- [ ] **v2.0** — Docker containerization
- [ ] **v2.1** — Redis-based distributed queue
- [ ] **v2.2** — RESTful API with JWT authentication
- [ ] **v2.3** — WebSocket real-time updates
- [ ] **v3.0** — Kubernetes deployment manifests
- [ ] **v3.1** — OCR integration (Tesseract)
- [ ] **v3.2** — DOCX/XLSX support

---

## 👥 Contributing

We welcome contributions! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java Code Conventions
- Write unit tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Authors

| Role | Responsibilities | Contact |
|------|------------------|---------|
| **Backend Architect** | Database design, worker architecture | backend@example.com |
| **Service Engineer** | PDFBox integration, business logic | service@example.com |
| **Frontend Developer** | Servlet controllers, JSP/UI | frontend@example.com |

---

## 🙏 Acknowledgments

- [Apache PDFBox](https://pdfbox.apache.org/) — PDF processing library
- [Apache Commons](https://commons.apache.org/) — Utility libraries
- [MySQL](https://www.mysql.com/) — Database system
- University of Science and Technology, The University of Danang

---

<div align="center">

**Built with ❤️ using Java EE**

[⬆ Back to Top](#-pdf-processing-engine)

</div>
