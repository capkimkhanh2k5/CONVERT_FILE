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
            max-width: 900px;
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

        .submit-btn:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
        }

        .submit-btn:active:not(:disabled) {
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

        /* Results Section */
        .results-section {
            margin-top: 40px;
            padding-top: 40px;
            border-top: 3px solid #e0e0e0;
            display: none;
            animation: fadeInUp 0.5s ease;
        }

        .results-section.show {
            display: block;
        }

        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .results-header {
            text-align: center;
            margin-bottom: 30px;
        }

        .results-icon {
            font-size: 50px;
            margin-bottom: 10px;
            animation: bounce 1s ease infinite;
        }

        @keyframes bounce {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-10px); }
        }

        .results-title {
            font-size: 24px;
            color: #333;
            font-weight: 700;
            margin-bottom: 5px;
        }

        .results-subtitle {
            color: #666;
            font-size: 14px;
        }

        .stats-container {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            padding: 25px;
            border-radius: 16px;
            text-align: center;
            transition: all 0.3s ease;
            border: 2px solid transparent;
        }

        .stat-card:hover {
            border-color: #667eea;
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
        }

        .stat-card-icon {
            font-size: 36px;
            margin-bottom: 10px;
        }

        .stat-card-label {
            font-size: 13px;
            color: #666;
            margin-bottom: 8px;
            font-weight: 500;
        }

        .stat-card-value {
            font-size: 32px;
            color: #333;
            font-weight: 700;
        }

        .progress-bar {
            height: 6px;
            background: rgba(255, 255, 255, 0.5);
            border-radius: 3px;
            margin-top: 10px;
            overflow: hidden;
        }

        .progress-fill {
            height: 100%;
            background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
            border-radius: 3px;
            transition: width 1s ease;
        }

        .results-table-container {
            background: white;
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
        }

        .results-table {
            width: 100%;
            border-collapse: collapse;
        }

        .results-table thead {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        .results-table th {
            padding: 18px 20px;
            text-align: left;
            font-weight: 600;
            font-size: 14px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .results-table td {
            padding: 16px 20px;
            border-bottom: 1px solid #f0f0f0;
            color: #333;
        }

        .results-table tbody tr {
            transition: background 0.2s ease;
        }

        .results-table tbody tr:hover {
            background: #f5f7fa;
        }

        .results-table tbody tr:last-child td {
            border-bottom: none;
        }

        .file-name-cell {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .file-name-icon {
            font-size: 24px;
        }

        .status-badge {
            display: inline-block;
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
        }

        .status-badge.success {
            background: #e8f5e9;
            color: #2e7d32;
        }

        .status-badge.processing {
            background: #fff3e0;
            color: #ef6c00;
            animation: pulse 1.5s ease-in-out infinite;
        }

        @keyframes pulse {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.6; }
        }

        .status-badge.error {
            background: #ffebee;
            color: #c62828;
        }

        .download-cell {
            text-align: center;
        }

        .download-btn-small {
            background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
        }

        .download-btn-small:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(76, 175, 80, 0.4);
        }

        .download-btn-small:disabled {
            opacity: 0.5;
            cursor: not-allowed;
            background: #ccc;
        }

        .download-all-btn {
            width: 100%;
            padding: 16px;
            background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
            color: white;
            border: none;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(76, 175, 80, 0.4);
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
        }

        .download-all-btn:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(76, 175, 80, 0.6);
        }

        .download-all-btn:disabled {
            opacity: 0.5;
            cursor: not-allowed;
            background: #ccc;
        }

        @media (max-width: 768px) {
            .container {
                padding: 30px 20px;
            }

            h2 {
                font-size: 24px;
            }

            .upload-area {
                padding: 40px 20px;
            }

            .stats-container {
                grid-template-columns: 1fr;
            }

            .results-table-container {
                overflow-x: auto;
            }

            .results-table {
                min-width: 600px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>📁 Upload File</h2>
        <p class="subtitle">Tải lên PDF hoặc ZIP để chuyển đổi</p>

        <form id="uploadForm">
            <div class="upload-area" id="uploadArea">
                <div class="upload-icon">☁️</div>
                <div class="upload-text">Kéo thả file vào đây</div>
                <div class="upload-text">hoặc nhấn để chọn file</div>
                <div class="file-types">Hỗ trợ: PDF, ZIP (Tối đa 50MB)</div>
                <input type="file" name="file" id="fileInput" accept=".pdf,.zip" />
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

        <!-- Results Section -->
        <div class="results-section" id="resultsSection">
            <div class="results-header">
                <div class="results-icon">✅</div>
                <div class="results-title">Kết quả chuyển đổi</div>
                <div class="results-subtitle">Quá trình xử lý đã hoàn tất</div>
            </div>

            <div class="stats-container">
                <div class="stat-card">
                    <div class="stat-card-icon">📊</div>
                    <div class="stat-card-label">Trạng thái</div>
                    <div class="stat-card-value" id="statusValue">Đang xử lý</div>
                </div>

                <div class="stat-card">
                    <div class="stat-card-icon">📁</div>
                    <div class="stat-card-label">Tổng số file</div>
                    <div class="stat-card-value" id="totalFiles">5</div>
                </div>

                <div class="stat-card">
                    <div class="stat-card-icon">⏳</div>
                    <div class="stat-card-label">Hoàn thành</div>
                    <div class="stat-card-value"><span id="completedFiles">0</span>/<span id="totalFilesProgress">5</span></div>
                    <div class="progress-bar">
                        <div class="progress-fill" id="progressFill"></div>
                    </div>
                </div>
            </div>

            <div class="results-table-container">
                <table class="results-table">
                    <thead>
                        <tr>
                            <th>Tên file</th>
                            <th>Kích thước</th>
                            <th>Trạng thái</th>
                            <th style="text-align: center;">Tải xuống</th>
                        </tr>
                    </thead>
                    <tbody id="resultsTableBody">
                        <tr>
                            <td>
                                <div class="file-name-cell">
                                    <span class="file-name-icon">📄</span>
                                    <span>document_001.pdf</span>
                                </div>
                            </td>
                            <td>2.5 MB</td>
                            <td><span class="status-badge processing">Đang xử lý</span></td>
                            <td class="download-cell">
                                <button class="download-btn-small" disabled>⏳ Đang xử lý...</button>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div class="file-name-cell">
                                    <span class="file-name-icon">📄</span>
                                    <span>report_2024.pdf</span>
                                </div>
                            </td>
                            <td>1.8 MB</td>
                            <td><span class="status-badge processing">Đang xử lý</span></td>
                            <td class="download-cell">
                                <button class="download-btn-small" disabled>⏳ Đang xử lý...</button>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div class="file-name-cell">
                                    <span class="file-name-icon">📄</span>
                                    <span>presentation.pdf</span>
                                </div>
                            </td>
                            <td>4.2 MB</td>
                            <td><span class="status-badge processing">Đang xử lý</span></td>
                            <td class="download-cell">
                                <button class="download-btn-small" disabled>⏳ Đang xử lý...</button>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div class="file-name-cell">
                                    <span class="file-name-icon">📄</span>
                                    <span>invoice_march.pdf</span>
                                </div>
                            </td>
                            <td>0.8 MB</td>
                            <td><span class="status-badge processing">Đang xử lý</span></td>
                            <td class="download-cell">
                                <button class="download-btn-small" disabled>⏳ Đang xử lý...</button>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div class="file-name-cell">
                                    <span class="file-name-icon">📄</span>
                                    <span>contract_final.pdf</span>
                                </div>
                            </td>
                            <td>3.1 MB</td>
                            <td><span class="status-badge processing">Đang xử lý</span></td>
                            <td class="download-cell">
                                <button class="download-btn-small" disabled>⏳ Đang xử lý...</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <button class="download-all-btn" id="downloadAllBtn" disabled>
                <span>⏳</span>
                <span>Đang xử lý...</span>
            </button>
        </div>
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
        const resultsSection = document.getElementById('resultsSection');
        const progressFill = document.getElementById('progressFill');

        uploadArea.addEventListener('click', () => {
            fileInput.click();
        });

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
                const dt = new DataTransfer();
                dt.items.add(files[0]);
                fileInput.files = dt.files;
                handleFileSelect();
            }
        });

        fileInput.addEventListener('change', handleFileSelect);

        function handleFileSelect() {
            const file = fileInput.files[0];
            if (file) {
                const validTypes = ['application/pdf', 'application/zip', 'application/x-zip-compressed'];
                if (!validTypes.includes(file.type) && !file.name.match(/\.(pdf|zip)$/i)) {
                    showMessage('Vui lòng chọn file PDF hoặc ZIP!', 'error');
                    fileInput.value = '';
                    return;
                }

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

        removeFile.addEventListener('click', () => {
            fileInput.value = '';
            fileInfo.classList.remove('show');
            submitBtn.disabled = true;
        });

        function formatFileSize(bytes) {
            if (bytes === 0) return '0 Bytes';
            const k = 1024;
            const sizes = ['Bytes', 'KB', 'MB', 'GB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
        }

        function showMessage(text, type) {
            messageDiv.textContent = text;
            messageDiv.className = 'message show ' + type;
        }

        function hideMessage() {
            messageDiv.classList.remove('show');
        }

        uploadForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const formData = new FormData(uploadForm);
            
            try {
                // Upload file
                const uploadResponse = await fetch('upload', {
                    method: 'POST',
                    body: formData
                });
                
                const uploadResult = await uploadResponse.json();
                
                if(uploadResult.statusProgress === 'PROCESSING') {
                    resultsSection.classList.add('show');
                    
                    startPolling();
                }
            } catch(error) {
                showMessage('Lỗi: ' + error.message, 'error');
            }
        });

        function startPolling() {
            const pollingInterval = setInterval(async () => {
                try {
                    const response = await fetch('upload'); // GET request
                    const data = await response.json();
                    
                    if(data.statusProgress === 'SUCCESS') {
                        // Cập nhật UI với data.files
                        updateResultsTable(data.files);
                        updateStats(data.totalFiles, data.completedFiles);
                        
                        // Nếu tất cả đã xong, dừng polling
                        if(data.completedFiles === data.totalFiles) {
                            clearInterval(pollingInterval);
                            enableDownloadButtons();
                        }
                    }
                } catch(error) {
                    console.error('Polling error:', error);
                }
            }, 1000); // Poll mỗi giây
        }

        // Download button handlers
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('download-btn-small') && !e.target.disabled) {
                const fileName = e.target.closest('tr').querySelector('.file-name-cell span:last-child').textContent;
                showMessage(`📥 Đang tải xuống: ${fileName}`, 'success');
            }
        });

        document.getElementById('downloadAllBtn').addEventListener('click', () => {
            if (!document.getElementById('downloadAllBtn').disabled) {
                showMessage('📦 Đang chuẩn bị tải xuống tất cả file...', 'success');
            }
        });
    </script>
</body>
</html>