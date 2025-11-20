<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload File - ConvertFile</title>

    <!-- Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700;800&display=swap"
        rel="stylesheet">

    <style>
        :root {
            --primary: #6366f1;
            --secondary: #ec4899;
            --accent: #8b5cf6;
            --text-main: #1e293b;
            --text-light: #64748b;
            --bg-glass: rgba(255, 255, 255, 0.8);
            --border-glass: 1px solid rgba(255, 255, 255, 0.3);
            --shadow-glass: 0 8px 32px 0 rgba(31, 38, 135, 0.1);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Outfit', sans-serif;
        }

        body {
            min-height: 100vh;
            background: #f0f2f5;
            color: var(--text-main);
            overflow: hidden;
            display: flex;
            flex-direction: column;
        }

        /* Animated Background */
        .bg-animation {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            z-index: -1;
            overflow: hidden;
            background: linear-gradient(45deg, #f3f4f6, #e5e7eb);
        }

        .orb {
            position: absolute;
            border-radius: 50%;
            filter: blur(80px);
            opacity: 0.6;
            animation: float 20s infinite ease-in-out;
        }

        .orb-1 {
            width: 500px;
            height: 500px;
            background: var(--primary);
            top: -10%;
            left: -10%;
        }

        .orb-2 {
            width: 400px;
            height: 400px;
            background: var(--secondary);
            bottom: -10%;
            right: -10%;
            animation-delay: -5s;
        }

        .orb-3 {
            width: 300px;
            height: 300px;
            background: var(--accent);
            top: 40%;
            left: 40%;
            animation-delay: -10s;
        }

        @keyframes float {

            0%,
            100% {
                transform: translate(0, 0) scale(1);
            }

            50% {
                transform: translate(20px, -20px) scale(1.1);
            }
        }

        /* Header */
        header {
            background: rgba(255, 255, 255, 0.8);
            backdrop-filter: blur(20px);
            padding: 16px 0;
            position: sticky;
            top: 0;
            z-index: 100;
            border-bottom: 1px solid rgba(255, 255, 255, 0.5);
        }

        .container {
            max-width: 1000px;
            margin: 0 auto;
            padding: 0 24px;
        }

        nav {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .logo {
            font-size: 24px;
            font-weight: 800;
            background: linear-gradient(135deg, var(--primary), var(--secondary));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            text-decoration: none;
        }

        .nav-link {
            color: var(--primary);
            font-weight: 600;
            text-decoration: none;
            font-size: 14px;
            transition: all 0.3s;
        }

        .nav-link:hover {
            color: var(--secondary);
            transform: translateX(5px);
        }

        /* Main Content */
        .upload-container {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .glass-card {
            background: var(--bg-glass);
            backdrop-filter: blur(20px);
            border-radius: 24px;
            border: var(--border-glass);
            box-shadow: var(--shadow-glass);
            padding: 48px;
            width: 100%;
            max-width: 500px;
            text-align: center;
        }

        .glass-card h2 {
            font-size: 28px;
            margin-bottom: 8px;
            color: var(--text-main);
        }

        .glass-card p {
            color: var(--text-light);
            margin-bottom: 32px;
        }

        /* File Upload */
        .file-upload-wrapper {
            position: relative;
            width: 100%;
            height: 160px;
            border: 2px dashed #cbd5e1;
            border-radius: 20px;
            background: rgba(255, 255, 255, 0.5);
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            margin-bottom: 24px;
            transition: all 0.3s ease;
            cursor: pointer;
        }

        .file-upload-wrapper:hover {
            border-color: var(--primary);
            background: rgba(99, 102, 241, 0.05);
            transform: scale(1.02);
        }

        .file-upload-wrapper input {
            position: absolute;
            width: 100%;
            height: 100%;
            opacity: 0;
            cursor: pointer;
        }

        .icon-upload {
            font-size: 48px;
            margin-bottom: 12px;
        }

        .upload-text {
            font-weight: 600;
            color: var(--text-main);
        }

        .upload-subtext {
            font-size: 12px;
            color: var(--text-light);
            margin-top: 4px;
        }

        /* Form Elements */
        .form-label {
            display: block;
            text-align: left;
            font-weight: 600;
            color: var(--text-main);
            margin-bottom: 8px;
            font-size: 14px;
        }

        .form-select {
            width: 100%;
            padding: 12px 16px;
            border-radius: 12px;
            border: 1px solid #e2e8f0;
            background: white;
            color: var(--text-main);
            font-size: 14px;
            outline: none;
            margin-bottom: 24px;
            transition: all 0.3s;
        }

        .form-select:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
        }

        .btn-convert {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, var(--primary), var(--accent));
            color: white;
            border: none;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 700;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
        }

        .btn-convert:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(99, 102, 241, 0.4);
        }
    </style>
</head>

<body>

    <div class="bg-animation">
        <div class="orb orb-1"></div>
        <div class="orb orb-2"></div>
        <div class="orb orb-3"></div>
    </div>

    <header>
        <div class="container">
            <nav>
                <a href="<c:url value='/home'/>" class="logo">ConvertFile</a>
                <a href="jobs" class="nav-link">View Progress →</a>
            </nav>
        </div>
    </header>

    <div class="upload-container">
        <div class="glass-card">
            <h2>Upload Files</h2>
            <p>Choose your file and let us do the magic ✨</p>

            <form action="upload" method="post" enctype="multipart/form-data">

                <div class="file-upload-wrapper">
                    <input type="file" name="file" required onchange="updateFileName(this)">
                    <div class="icon-upload">☁️</div>
                    <div class="upload-text" id="fileName">Drag & Drop or Click to Browse</div>
                    <div class="upload-subtext">Supports PDF, DOCX, Images</div>
                </div>

                <label class="form-label">Convert to:</label>
                <select name="taskType" class="form-select">
                    <option value="PDF_TO_DOCX">PDF ➝ Word Document</option>
                    <option value="DOCX_TO_PDF">Word ➝ PDF Document</option>
                    <option value="IMG_TO_PDF">Image ➝ PDF Document</option>
                    <option value="PDF_TO_IMG">PDF ➝ Image</option>
                </select>

                <button type="submit" class="btn-convert">🚀 Start Conversion</button>
            </form>
        </div>
    </div>

    <script>
        function updateFileName(input) {
            const fileNameDisplay = document.getElementById('fileName');
            if (input.files && input.files.length > 0) {
                fileNameDisplay.innerText = input.files[0].name;
                fileNameDisplay.style.color = 'var(--primary)';
            } else {
                fileNameDisplay.innerText = "Drag & Drop or Click to Browse";
                fileNameDisplay.style.color = 'var(--text-main)';
            }
        }
    </script>

</body>

</html>