<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<meta charset="UTF-8">

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ConvertZone</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: #f5f7fa;
            color: #2d3748;
            overflow-x: hidden;
        }

        /* Layout chính */
        .app-container {
            display: grid;
            grid-template-columns: 240px 1fr 320px;
            height: 100vh;
        }

        /* Sidebar trái */
        .sidebar {
            background: white;
            border-right: 1px solid #e2e8f0;
            display: flex;
            flex-direction: column;
            padding: 24px 16px;
        }

        .user-profile {
            text-align: center;
            margin-bottom: 32px;
            padding-bottom: 24px;
            border-bottom: 1px solid #e2e8f0;
        }

        .avatar {
            width: 64px;
            height: 64px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            margin: 0 auto 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 28px;
            color: white;
        }

        .user-name {
            font-weight: 600;
            font-size: 15px;
            color: #1a202c;
            margin-bottom: 4px;
        }

        .user-email {
            font-size: 12px;
            color: #718096;
        }

        .nav-menu {
            flex: 1;
        }

        .nav-item {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px 16px;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.2s;
            margin-bottom: 4px;
            color: #4a5568;
            font-size: 14px;
        }

        .nav-item:hover {
            background: #f7fafc;
            color: #667eea;
        }

        .nav-item.active {
            background: #eef2ff;
            color: #667eea;
            font-weight: 500;
        }

        .nav-icon {
            font-size: 20px;
            width: 20px;
            text-align: center;
        }

        .nav-bottom {
            margin-top: auto;
            padding-top: 16px;
            border-top: 1px solid #e2e8f0;
        }

        /* Main content */
        .main-content {
            padding: 32px 48px;
            overflow-y: auto;
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 32px;
        }

        .page-title {
            font-size: 24px;
            font-weight: 600;
            color: #1a202c;
        }

        .header-actions {
            display: flex;
            gap: 8px;
        }

        .icon-btn {
            width: 36px;
            height: 36px;
            border-radius: 8px;
            border: none;
            background: white;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 18px;
            transition: all 0.2s;
        }

        .icon-btn:hover {
            background: #f7fafc;
        }

        /* Upload area */
        .upload-container {
            background: white;
            border-radius: 16px;
            padding: 48px;
            margin-bottom: 32px;
        }

        .upload-area {
            border: 2px dashed #cbd5e0;
            border-radius: 12px;
            padding: 64px 32px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s;
            background: #f7fafc;
        }

        .upload-area:hover {
            border-color: #667eea;
            background: #eef2ff;
        }

        .upload-area.dragover {
            border-color: #667eea;
            background: #eef2ff;
            border-style: solid;
        }

        .upload-icon {
            width: 80px;
            height: 80px;
            margin: 0 auto 24px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 40px;
            color: white;
        }

        .upload-text {
            font-size: 16px;
            color: #2d3748;
            margin-bottom: 8px;
        }

        .upload-hint {
            font-size: 13px;
            color: #718096;
            margin-bottom: 24px;
        }

        .browse-btn {
            background: #667eea;
            color: white;
            border: none;
            padding: 12px 32px;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.2s;
        }

        .browse-btn:hover {
            background: #5568d3;
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
        }

        input[type="file"] {
            display: none;
        }

        /* Converted files section */
        .converted-files {
            background: white;
            border-radius: 16px;
            padding: 32px;
        }

        .file-item {
            display: flex;
            align-items: center;
            gap: 16px;
            padding: 16px;
            border-radius: 8px;
            margin-bottom: 12px;
            transition: all 0.2s;
        }

        .file-item:hover {
            background: #f7fafc;
        }

        .file-icon-wrapper {
            width: 40px;
            height: 40px;
            background: #f7fafc;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
        }

        .file-info {
            flex: 1;
        }

        .file-name {
            font-size: 14px;
            font-weight: 500;
            color: #2d3748;
            margin-bottom: 4px;
        }

        .file-size {
            font-size: 12px;
            color: #718096;
        }

        .status-badge {
            padding: 6px 12px;
            border-radius: 6px;
            font-size: 11px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .status-badge.converted {
            background: #c6f6d5;
            color: #22543d;
        }

        .download-icon {
            width: 32px;
            height: 32px;
            border-radius: 6px;
            border: 1px solid #e2e8f0;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.2s;
            font-size: 16px;
        }

        .download-icon:hover {
            background: #f7fafc;
            border-color: #cbd5e0;
        }

        /* Right sidebar - History */
        .history-panel {
            background: white;
            border-left: 1px solid #e2e8f0;
            padding: 24px 16px;
            overflow-y: auto;
        }

        .history-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
        }

        .history-title {
            font-size: 16px;
            font-weight: 600;
            color: #1a202c;
        }

        .history-section {
            margin-bottom: 24px;
        }

        .history-date {
            font-size: 12px;
            font-weight: 600;
            color: #718096;
            text-transform: uppercase;
            margin-bottom: 12px;
        }

        .history-item {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 8px;
            cursor: pointer;
            transition: all 0.2s;
        }

        .history-item:hover {
            background: #f7fafc;
        }

        .history-icon {
            width: 40px;
            height: 40px;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            flex-shrink: 0;
        }

        .history-icon.purple { background: #e9d5ff; }
        .history-icon.yellow { background: #fef3c7; }
        .history-icon.blue { background: #dbeafe; }
        .history-icon.orange { background: #fed7aa; }
        .history-icon.green { background: #d1fae5; }

        .history-file-info {
            flex: 1;
            min-width: 0;
        }

        .history-file-name {
            font-size: 13px;
            font-weight: 500;
            color: #2d3748;
            margin-bottom: 2px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .history-file-size {
            font-size: 11px;
            color: #718096;
        }

        /* Recommended Tools */
        .recommended-tools {
            background: white;
            border-radius: 16px;
            padding: 32px;
        }

        .tools-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
        }

        .tools-title {
            font-size: 18px;
            font-weight: 600;
            color: #1a202c;
        }

        .tools-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 16px;
        }

        .tool-card {
            padding: 20px;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            cursor: pointer;
            transition: all 0.2s;
        }

        .tool-card:hover {
            border-color: #667eea;
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
            transform: translateY(-2px);
        }

        .tool-icon {
            width: 48px;
            height: 48px;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            margin-bottom: 12px;
        }

        .tool-icon.green { background: #d1fae5; }
        .tool-icon.orange { background: #fed7aa; }
        .tool-icon.blue { background: #dbeafe; }
        .tool-icon.yellow { background: #fef3c7; }
        .tool-icon.purple { background: #e9d5ff; }
        .tool-icon.red { background: #fecaca; }

        .tool-name {
            font-size: 14px;
            font-weight: 600;
            color: #2d3748;
            margin-bottom: 4px;
        }

        .tool-description {
            font-size: 12px;
            color: #718096;
            line-height: 1.4;
        }

        @media (max-width: 1200px) {
            .app-container {
                grid-template-columns: 240px 1fr;
            }
            .history-panel {
                display: none;
            }
        }

        @media (max-width: 768px) {
            .app-container {
                grid-template-columns: 1fr;
            }
            .sidebar {
                display: none;
            }
            .main-content {
                padding: 20px;
            }
            .tools-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }
    </style>
</head>
<body>
    <div class="app-container">
        <!-- Sidebar trái -->
        <aside class="sidebar">
            <div class="user-profile">
                <div class="avatar">👤</div>
                <div class="user-name">Guest</div>
                <div class="user-email">guest@gmail.com</div>
            </div>

            <nav class="nav-menu">
                <div class="nav-item active" data-view="home">
                    <span class="nav-icon">📚</span>
                    <span>Home</span>
                </div>
                <div class="nav-item" data-view="tools">
                    <span class="nav-icon">🛠️</span>
                    <span>All Tools</span>
                </div>
                <div class="nav-item" data-view="history">
                    <span class="nav-icon">🕒</span>
                    <span>History</span>
                </div>
            </nav>

            <div class="nav-bottom">
                <div class="nav-item">
                    <span class="nav-icon">⚙️</span>
                    <span>Setting</span>
                </div>
                <div class="nav-item">
                    <span class="nav-icon">❓</span>
                    <span>Help Center</span>
                </div>
                <div class="nav-item">
                    <span class="nav-icon">🚪</span>
                    <span>Logout</span>
                </div>
            </div>
        </aside>

        <!-- Main content -->
        <main class="main-content">
            <div class="header">
                <h1 class="page-title">PDF to Word</h1>
                <div class="header-actions">
                    <button class="icon-btn">⋯</button>
                </div>
            </div>

            <!-- Upload Area -->
            <div class="upload-container">
                <div class="upload-area" id="uploadArea">
                    <div class="upload-icon">☁️</div>
                    <div class="upload-text">Drag your files here, file size less than 15 MB or</div>
                    <div class="upload-hint"></div>
                    <button class="browse-btn" onclick="document.getElementById('fileInput').click()">Browse File</button>
                    <input type="file" id="fileInput" accept=".pdf,.zip" multiple>
                </div>
            </div>

            <!-- Converted Files -->
            <div class="converted-files">
                <div class="file-item">
                    <div class="file-icon-wrapper">📄</div>
                    <div class="file-info">
                        <div class="file-name">Biography Nelson Mandela.doc</div>
                    </div>
                    <span class="status-badge converted">Converted</span>
                    <span class="file-size">2.31 mb</span>
                    <div class="download-icon">↓</div>
                </div>

                <div class="file-item">
                    <div class="file-icon-wrapper">📄</div>
                    <div class="file-info">
                        <div class="file-name">How to Choose The Most Fresh Fish.doc</div>
                    </div>
                    <span class="status-badge converted">Converted</span>
                    <span class="file-size">1.35 mb</span>
                    <div class="download-icon">↓</div>
                </div>

                <div class="file-item">
                    <div class="file-icon-wrapper">📄</div>
                    <div class="file-info">
                        <div class="file-name">Journal of Business and Research.doc</div>
                    </div>
                    <span class="status-badge converted">Converted</span>
                    <span class="file-size">4.11 mb</span>
                    <div class="download-icon">↓</div>
                </div>
            </div>

            <!-- Recommended Tools -->
            <div class="recommended-tools">
                <div class="tools-header">
                    <h2 class="tools-title">Recommended Tools</h2>
                    <button class="icon-btn">⋯</button>
                </div>

                <div class="tools-grid">
                    <div class="tool-card">
                        <div class="tool-icon green">📄</div>
                        <div class="tool-name">Word to Excel</div>
                        <div class="tool-description">Convert any Word file to Excel instantly</div>
                    </div>

                    <div class="tool-card">
                        <div class="tool-icon orange">📄</div>
                        <div class="tool-name">PNG to PDF</div>
                        <div class="tool-description">Convert any PNG file to PDF instantly</div>
                    </div>

                    <div class="tool-card">
                        <div class="tool-icon blue">📄</div>
                        <div class="tool-name">Word to PDF</div>
                        <div class="tool-description">Convert any Word file to PDF instantly</div>
                    </div>

                    <div class="tool-card">
                        <div class="tool-icon yellow">🎨</div>
                        <div class="tool-name">AI to SVG</div>
                        <div class="tool-description">Convert any AI file to SVG instantly</div>
                    </div>

                    <div class="tool-card">
                        <div class="tool-icon purple">🎨</div>
                        <div class="tool-name">PNG to SVG</div>
                        <div class="tool-description">Convert any PNG file to SVG instantly</div>
                    </div>

                    <div class="tool-card">
                        <div class="tool-icon red">📊</div>
                        <div class="tool-name">Excel to PDF</div>
                        <div class="tool-description">Convert any Excel file to PDF instantly</div>
                    </div>
                </div>
            </div>
        </main>

        <!-- History Panel -->
        <aside class="history-panel">
            <div class="history-header">
                <h2 class="history-title">History</h2>
                <button class="icon-btn">⋯</button>
            </div>

            <div class="history-section">
                <div class="history-date">Today</div>
                
                <div class="history-item">
                    <div class="history-icon purple">📄</div>
                    <div class="history-file-info">
                        <div class="history-file-name">Task 2 Math ; Essay.doc</div>
                        <div class="history-file-size">2.1 mb</div>
                    </div>
                </div>

                <div class="history-item">
                    <div class="history-icon purple">🖼️</div>
                    <div class="history-file-info">
                        <div class="history-file-name">Graduation 13/01/22.png</div>
                        <div class="history-file-size">5.31 mb</div>
                    </div>
                </div>

                <div class="history-item">
                    <div class="history-icon yellow">🎨</div>
                    <div class="history-file-info">
                        <div class="history-file-name">Scenery Illustration.svg</div>
                        <div class="history-file-size">7.11 mb</div>
                    </div>
                </div>

                <div class="history-item">
                    <div class="history-icon blue">📄</div>
                    <div class="history-file-info">
                        <div class="history-file-name">Task 2 Math ; Essay.doc</div>
                        <div class="history-file-size">1.09 mb</div>
                    </div>
                </div>

                <div class="history-item">
                    <div class="history-icon orange">📊</div>
                    <div class="history-file-info">
                        <div class="history-file-name">Poster.ai</div>
                        <div class="history-file-size">5.8 mb</div>
                    </div>
                </div>
            </div>

            <div class="history-section">
                <div class="history-date">Yesterday</div>
                
                <div class="history-item">
                    <div class="history-icon green">📊</div>
                    <div class="history-file-info">
                        <div class="history-file-name">Financial Report.xls</div>
                        <div class="history-file-size">1.22 mb</div>
                    </div>
                </div>

                <div class="history-item">
                    <div class="history-icon orange">🎨</div>
                    <div class="history-file-info">
                        <div class="history-file-name">Animal Illustration.ai</div>
                        <div class="history-file-size">6.13 mb</div>
                    </div>
                </div>
            </div>
        </aside>
    </div>

    <script>
        const uploadArea = document.getElementById('uploadArea');
        const fileInput = document.getElementById('fileInput');

        // Drag and drop functionality
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
            handleFiles(files);
        });

        uploadArea.addEventListener('click', (e) => {
            if (e.target.tagName !== 'BUTTON') {
                fileInput.click();
            }
        });

        fileInput.addEventListener('change', (e) => {
            handleFiles(e.target.files);
        });

        function handleFiles(files) {
            if (files.length > 0) {
                console.log('Files selected:', files);
                // Add your file handling logic here
            }
        }
    </script>
</body>
</html>