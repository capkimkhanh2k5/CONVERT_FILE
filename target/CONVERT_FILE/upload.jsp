<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>File Upload - CONVERT_FILE</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .container {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 24px;
            padding: 50px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            max-width: 500px;
            width: 100%;
            animation: slideUp 0.5s ease-out;
        }

        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        h2 {
            color: #333;
            font-size: 28px;
            margin-bottom: 10px;
            text-align: center;
            font-weight: 700;
        }

        .subtitle {
            text-align: center;
            color: #666;
            margin-bottom: 40px;
            font-size: 14px;
        }

        .upload-area {
            border: 3px dashed #667eea;
            border-radius: 16px;
            padding: 50px 30px;
            text-align: center;
            transition: all 0.3s ease;
            cursor: pointer;
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            position: relative;
            overflow: hidden;
        }

        .upload-area:hover {
            border-color: #764ba2;
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
        }

        .upload-area.dragover {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-color: #fff;
        }

        .upload-area.dragover .upload-icon,
        .upload-area.dragover .upload-text,
        .upload-area.dragover .file-types {
            color: #fff;
        }

        .upload-icon {
            font-size: 60px;
            margin-bottom: 20px;
            color: #667eea;
            transition: all 0.3s ease;
        }

        .upload-area:hover .upload-icon {
            transform: scale(1.1);
        }

        .upload-text {
            font-size: 18px;
            color: #333;
            margin-bottom: 10px;
            font-weight: 600;
        }

        .file-types {
            font-size: 14px;
            color: #666;
            margin-top: 10px;
        }

        input[type="file"] {
            display: none;
        }

        .file-info {
            margin-top: 20px;
            padding: 15px;
            background: #f0f4ff;
            border-radius: 12px;
            display: none;
            align-items: center;
            gap: 15px;
        }

        .file-info.show {
            display: flex;
            animation: fadeIn 0.3s ease;
        }

        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }

        .file-icon {
            font-size: 40px;
        }

        .file-details {
            flex: 1;
        }

        .file-name {
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
        }

        .file-size {
            font-size: 13px;
            color: #666;
        }

        .remove-file {
            background: none;
            border: none;
            color: #f44336;
            cursor: pointer;
            font-size: 24px;
            padding: 5px;
            transition: transform 0.2s;
        }

        .remove-file:hover {
            transform: scale(1.2);
        }

        .submit-btn {
            width: 100%;
            padding: 16px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            margin-top: 25px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        }

        .submit-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
        }

        .submit-btn:active {
            transform: translateY(0);
        }

        .submit-btn:disabled {
            opacity: 0.5;
            cursor: not-allowed;
            transform: none;
        }

        .message {
            margin-top: 20px;
            padding: 15px;
            border-radius: 12px;
            text-align: center;
            font-weight: 500;
            display: none;
            animation: slideDown 0.3s ease;
        }

        @keyframes slideDown {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .message.show {
            display: block;
        }

        .message.success {
            background: #e8f5e9;
            color: #2e7d32;
            border: 2px solid #4caf50;
        }

        .message.error {
            background: #ffebee;
            color: #c62828;
            border: 2px solid #f44336;
        }

        @media (max-width: 600px) {
            .container {
                padding: 30px 20px;
            }

            h2 {
                font-size: 24px;
            }

            .upload-area {
                padding: 40px 20px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>📁 Upload File</h2>
        <p class="subtitle">Tải lên PDF hoặc ZIP để chuyển đổi</p>

        <form method="post" action="upload" enctype="multipart/form-data" id="uploadForm">
            <div class="upload-area" id="uploadArea">
                <div class="upload-icon">☁️</div>
                <div class="upload-text">Kéo thả file vào đây</div>
                <div class="upload-text">hoặc nhấn để chọn file</div>
                <div class="file-types">Hỗ trợ: PDF, ZIP (Tối đa 50MB)</div>
                <input type="file" name="file" id="fileInput" accept=".pdf,.zip" required />
            </div>

            <div class="file-info" id="fileInfo">
                <div class="file-icon">📄</div>
                <div class="file-details">
                    <div class="file-name" id="fileName"></div>
                    <div class="file-size" id="fileSize"></div>
                </div>
                <button type="button" class="remove-file" id="removeFile">×</button>
            </div>

            <button type="submit" class="submit-btn" id="submitBtn" disabled>
                Tải lên và Chuyển đổi
            </button>
        </form>

        <div class="message" id="message"></div>
    </div>

    <script>
        const uploadArea = document.getElementById('uploadArea');
        const fileInput = document.getElementById('fileInput');
        const fileInfo = document.getElementById('fileInfo');
        const fileName = document.getElementById('fileName');
        const fileSize = document.getElementById('fileSize');
        const removeFile = document.getElementById('removeFile');
        const submitBtn = document.getElementById('submitBtn');
        const uploadForm = document.getElementById('uploadForm');
        const messageDiv = document.getElementById('message');

        // Click to upload
        uploadArea.addEventListener('click', () => {
            fileInput.click();
        });

        // Drag and drop
        uploadArea.addEventListener('dragover', (e) => {
            e.preventDefault();
            uploadArea.classList.add('dragover');
        });

        uploadArea.addEventListener('dragleave', () => {
            uploadArea.classList.remove('dragover');
        });

        uploadArea.addEventListener('drop', (e) => {
            e.preventDefault();
            uploadArea.classList.remove('dragover');
            const files = e.dataTransfer.files;
            if (files.length > 0) {
                fileInput.files = files;
                handleFileSelect();
            }
        });

        // File input change
        fileInput.addEventListener('change', handleFileSelect);

        function handleFileSelect() {
            const file = fileInput.files[0];
            if (file) {
                // Validate file type
                const validTypes = ['application/pdf', 'application/zip', 'application/x-zip-compressed'];
                if (!validTypes.includes(file.type) && !file.name.match(/\.(pdf|zip)$/i)) {
                    showMessage('Vui lòng chọn file PDF hoặc ZIP!', 'error');
                    fileInput.value = '';
                    return;
                }

                // Validate file size (50MB)
                if (file.size > 50 * 1024 * 1024) {
                    showMessage('File không được vượt quá 50MB!', 'error');
                    fileInput.value = '';
                    return;
                }

                fileName.textContent = file.name;
                fileSize.textContent = formatFileSize(file.size);
                fileInfo.classList.add('show');
                submitBtn.disabled = false;
                hideMessage();
            }
        }

        // Remove file
        removeFile.addEventListener('click', () => {
            fileInput.value = '';
            fileInfo.classList.remove('show');
            submitBtn.disabled = true;
        });

        // Format file size
        function formatFileSize(bytes) {
            if (bytes === 0) return '0 Bytes';
            const k = 1024;
            const sizes = ['Bytes', 'KB', 'MB', 'GB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
        }

        // Show message
        function showMessage(text, type) {
            messageDiv.textContent = text;
            messageDiv.className = 'message show ' + type;
        }

        // Hide message
        function hideMessage() {
            messageDiv.classList.remove('show');
        }

        // Form submit
        uploadForm.addEventListener('submit', (e) => {
            submitBtn.textContent = 'Đang tải lên...';
            submitBtn.disabled = true;
        });

        // Check for server message (from JSP)
        window.addEventListener('load', () => {
            const urlParams = new URLSearchParams(window.location.search);
            const msg = urlParams.get('message');
            const status = urlParams.get('status');
            
            if (msg) {
                showMessage(decodeURIComponent(msg), status || 'success');
            }
        });
    </script>
</body>
</html>