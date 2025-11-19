<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.convertfile.model.dao.JobDAO" %>

<%
    // --- LOGIC THÔNG MINH: CHỈ RELOAD KHI CẦN THIẾT ---
    // Lấy userId từ Session
    Object uidObj = session.getAttribute("userId");
    long currentUserId = 0;
    if (uidObj != null) {
        currentUserId = (Long) uidObj;
    }

    boolean needReload = false;
    // Truyền ID vào hàm getAllJobs
    List<Map<String, Object>> listJobs = JobDAO.getAllJobs(currentUserId); // Lấy dữ liệu ngay tại đây
    
    if (listJobs != null) {
        for (Map<String, Object> job : listJobs) {
            String s = (String) job.get("status");
            // Nếu còn file nào đang quay quay -> Bật chế độ tự F5
            if ("WAITING".equals(s) || "PROCESSING".equals(s)) {
                needReload = true;
                break;
            }
        }
    }
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ConvertFile</title>

    <% if (needReload) { %>
        <meta http-equiv="refresh" content="2">
    <% } %>

    <style>
        html, body{
            height: 100%;
            min-height: 100%;
            margin: 0;
            padding: 0;
            overflow-x: hidden;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: #f5f7fa;
            color: #2d3748;
        }

        /* Layout chính */
        .app-container {
            display: grid;
            grid-template-columns: 240px 1fr 320px;
            height: 100vh;
            min-height: 100vh;
            width: 100vw;
            overflow: hidden; 
        }

        /* Sidebar trái */
        .sidebar {
            background: white;
            border-right: 1px solid #e2e8f0;
            display: flex;
            flex-direction: column;
            padding: 24px 16px;
            overflow-y: auto;
            height: 100vh;
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
            overflow: hidden;
        }

        .avatar img {
            width: 100%;
            height: 100%;
            object-fit: cover;
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

        .nav-item a.nav-link {
            text-decoration: none;
            color: inherit;
            cursor: pointer;
        }

        .nav-item a.nav-link:hover {
            opacity: 0.8;
        }

        /* Main content */
        .main-content {
            padding: 32px 48px;
            overflow-y: auto;
            height: 100vh;
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 32px;
        }

        .logo {
            text-decoration: none;
            margin-right: 16px;
            font-size: 28px;
            font-weight: bold;
            display: inline-block;
            transition: all 0.3s ease;
            position: relative;
        }

        .logo span:first-child {
            color: #5e35b1;
            transition: all 0.3s ease;
        }

        .logo span:last-child {
            color: #1e88e5;
            transition: all 0.3s ease;
        }

        .logo:hover {
            transform: scale(1.05);
            filter: drop-shadow(0 0 8px rgba(94, 53, 177, 0.4));
        }

        .logo:hover span:first-child {
            color: #7e57c2;
            text-shadow: 0 0 10px rgba(94, 53, 177, 0.5);
        }

        .logo:hover span:last-child {
            color: #42a5f5;
            text-shadow: 0 0 10px rgba(30, 136, 229, 0.5);
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
            height: 100vh;
            width: 320px;
            box-sizing: border-box;
            transition: opacity 0.32s ease, transform 0.32s ease, width 0.32s ease, padding 0.32s ease;
            transform: translateX(0);
            opacity: 1;
        }

        /* When history is hidden, collapse the right column and expand main area */
        .app-container.no-history {
            grid-template-columns: 240px 1fr;
        }

        .app-container.no-history .history-panel {
            width: 0;
            padding: 0 0;
            opacity: 0;
            transform: translateX(12px);
            pointer-events: none;
            overflow: hidden;
            border: none;
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

        .file-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
        }

        .file-title {
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

        /* Modal for All Tools */
        .modal-overlay {
            position: fixed;
            inset: 0;
            background: rgba(15, 23, 42, 0.45);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 1200;
            opacity: 0;
            pointer-events: none;
            transition: opacity 0.24s ease;
        }

        .modal-overlay.visible {
            opacity: 1;
            pointer-events: auto;
        }

        .tools-modal {
            background: white;
            width: 520px;
            max-width: calc(100% - 40px);
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(2,6,23,0.2);
            transform: translateY(8px) scale(0.99);
            transition: transform 0.28s cubic-bezier(.2,.9,.38,1), opacity 0.28s ease;
            opacity: 0;
            overflow: hidden;
        }

        .modal-overlay.visible .tools-modal {
            transform: translateY(0) scale(1);
            opacity: 1;
        }

        .tools-modal header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 16px 20px;
            border-bottom: 1px solid #edf2f7;
        }

        .tools-modal .modal-body {
            padding: 16px 20px 20px;
            max-height: 60vh;
            overflow: auto;
        }

        .tool-entry {
            display: flex;
            gap: 12px;
            align-items: center;
            padding: 12px;
            border-radius: 8px;
            transition: background 0.12s;
            cursor: default;
        }

        .tool-entry:hover { background: #f7fafc; }

        .tool-entry .tool-thumb { width: 44px; height: 44px; border-radius: 8px; display:flex; align-items:center; justify-content:center; font-size:20px; }

        .tool-entry .tool-meta { flex:1; min-width:0; }

        .tool-entry .tool-meta .name { font-weight:600; color:#1a202c; font-size:14px; }
        
        .tool-entry .tool-meta .desc { font-size:13px; color:#718096; }

        .tool-entry .tool-action { margin-left: 8px; }


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
                <div class="avatar">
                    <%
                        String username = (String) session.getAttribute("username");
                        String userpicture = (String) session.getAttribute("userpicture");
                        String useremail = (String) session.getAttribute("useremail");

                        if(userpicture != null && !userpicture.isEmpty()){
                            %>
                                <img src="<%= userpicture %>" alt="User Avatar">
                            <%
                        }else{
                            %>
                                👤
                            <%
                        }   
                    %>
                </div>
                <div class="user-name">
                    <%
                        if(username != null && !username.isEmpty()){
                            %>
                                <%= username %>
                            <%
                        }else{
                            %>
                                GUEST
                            <%
                        }   
                    %>
                </div>
                <div class="user-email">
                    <%
                        if(useremail != null && !useremail.isEmpty()){
                            %>
                                <%= useremail %>
                            <%
                        }else{
                            %>
                                guest_example@gmail.com
                            <%
                        }   
                    %>
                </div>
            </div>

            <nav class="nav-menu">
                <div class="nav-item active" data-view="home">
                    <span class="nav-icon">🏠</span>
                    <span>Home</span>
                </div>
                <div class="nav-item" data-view="tools">
                    <span class="nav-icon">🛠️</span>
                    <span>All Tools</span>
                </div>
                <div class="nav-item" data-view="history">
                    <span class="nav-icon">⌛</span>
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
                    <%
                        if (username != null && !username.isEmpty()){
                            %>
                                <a href="<c:url value='/logout'/>" class ="nav-link">
                                    <span class="nav-icon">⬅️</span>
                                    <span>Logout</span>
                                </a>
                            <%
                        }
                        else{
                            %>
                                <a href="<c:url value='/login'/>" class ="nav-link">
                                    <span class="nav-icon">➡️</span>
                                    <span>Login</span>
                                </a>
                            <%
                        }
                    %>
                </div>
            </div>
        </aside>

        <!-- Main content -->
        <main class="main-content">
            <div class="header">
                <h1 class="page-title">PDF to Word</h1>
                <a href="<c:url value='/index.jsp'/>" class="logo">
                    <span>Convert</span><span">File</span>
                </a>
            </div>

            <!-- Upload Area -->
            <div class="upload-container">
                <form id="realUploadForm" action="upload" method="post" enctype="multipart/form-data">
                    <div class="upload-area" id="uploadArea">
                        <div class="upload-icon">☁️</div>
                        <div class="upload-text">Drag your files here, file size less than 15 MB or</div>
                        <div class="upload-hint">Supports PDF, DOCX</div>
                        
                        <button type="button" class="browse-btn" onclick="document.getElementById('fileInput').click()">Browse File</button>
                        
                        <input type="file" name="file" id="fileInput" style="display: none;" 
                            onchange="document.getElementById('realUploadForm').submit()">
                        
                        <input type="hidden" name="taskType" value="PDF_TO_DOCX">
                    </div>
                </form>
            </div>

            <!-- Converted Files -->
            <div class="converted-files">
                <div class="file-header">
                    <h2 class="file-title">Uploaded Files</h2>
                </div>

                <%
                    if (listJobs != null && !listJobs.isEmpty()) {
                        int count = 0;
                        for (Map<String, Object> job : listJobs) {
                            if (count++ >= 5) break; // Chỉ hiện 5 cái

                            String status = (String) job.get("status");
                            int progress = (Integer) job.get("progress");
                            
                            // Logic màu sắc
                            String badgeClass = "status-badge";
                            String dlIcon = "...";
                            
                            if (status.equals("COMPLETED")) {
                                badgeClass += " converted";
                                dlIcon = "↓";
                            }
                %>
                        <div class="file-item">
                            <div class="file-icon-wrapper">📄</div>
                            <div class="file-info">
                                <div class="file-name"><%= job.get("name") %></div>
                                <div style="height: 4px; background: #eee; width: 100%; margin-top: 5px; border-radius: 2px;">
                                    <div style="height: 100%; background: #667eea; width: <%= progress %>%;"></div>
                                </div>
                            </div>
                            <span class="<%= badgeClass %>"><%= status %> <%= progress %>%</span>
                            <div class="download-icon">
                                <% if(status.equals("COMPLETED")) { %>
                                    <a href="download?file=<%= job.get("name") %>" class="download-icon" title="Download" style="text-decoration: none; color: inherit;">
                                        ↓
                                    </a>
                                <% } else { %>
                                    <div class="download-icon" title="Processing">...</div>
                                <% } %>
                            </div>
                        </div>
                <%      }
                    } else { %>
                        <div style="text-align: center; padding: 20px; color: #999;">Chưa có file nào.</div>
                <% } %>
            </div>

        </main>

        <!-- History Panel -->
        <aside class="history-panel">
            <div class="history-header">
                <h2 class="history-title">History</h2>
                <!-- <button class="icon-btn">⋯</button> -->
            </div>

            <div class="history-section">
                <div class="history-date">Recent Tasks</div>
                
                <%
                    if (listJobs != null) {
                        int hCount = 0;
                        for (Map<String, Object> job : listJobs) {
                            if (hCount++ >= 8) break; // Lấy 8 cái lịch sử
                %>
                    <div class="history-item">
                        <div class="history-icon purple">🕒</div>
                        <div class="history-file-info">
                            <div class="history-file-name"><%= job.get("name") %></div>
                            <div class="history-file-size"><%= job.get("type") %></div>
                        </div>
                    </div>
                <%      }
                    }
                %>
            </div>
        </aside>
        
        <!-- All Tools Modal (hidden by default) -->
        <div id="toolsModalOverlay" class="modal-overlay" aria-hidden="true">
            <div class="tools-modal" role="dialog" aria-modal="true" aria-labelledby="toolsModalTitle">
                <header>
                    <h3 id="toolsModalTitle" style="margin:0; font-size:16px;">All Tools</h3>
                    <div>
                        <button id="toolsModalClose" class="icon-btn" aria-label="Close tools">✕</button>
                    </div>
                </header>
                <div class="modal-body">
                    <!-- Example 3 tools template - edit as needed -->
                    <div id="toolGeneratedContainer" style="margin-bottom:12px;"></div>

                    <div class="tool-entry">
                        <div class="tool-thumb blue">📄</div>
                        <div class="tool-meta">
                            <div class="name">PDF to Word</div>
                            <div class="desc">Convert PDF files to editable Word documents</div>
                        </div>
                        <div class="tool-action"><button class="browse-btn" input="PDF" output="Word">Use</button></div>
                    </div>

                    <div class="tool-entry">
                        <div class="tool-thumb green">🖼️</div>
                        <div class="tool-meta">
                            <div class="name">PNG to PDF</div>
                            <div class="desc">Convert PNG images into a single PDF</div>
                        </div>
                        <div class="tool-action"><button class="browse-btn" input="PNG" output="PDF">Use</button></div>
                    </div>

                    <div class="tool-entry">
                        <div class="tool-thumb purple">🔗</div>
                        <div class="tool-meta">
                            <div class="name">Word to Excel</div>
                            <div class="desc">Extract tables from Word into Excel files</div>
                        </div>
                        <div class="tool-action"><button class="browse-btn" input="Word" output="Excel">Use</button></div>
                    </div>
                </div>
            </div>
        </div>
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

        // --- View toggling: hide/show History panel based on left nav clicks ---
        (function(){
            const navItems = document.querySelectorAll('.nav-item[data-view]');
            const appContainer = document.querySelector('.app-container');

            function clearActive() {
                navItems.forEach(i => i.classList.remove('active'));
            }

            function setActiveByName(name) {
                navItems.forEach(item => {
                    const v = item.dataset.view || '';
                    item.classList.toggle('active', v === name);
                });
            }

            // Toggle logic: clicking History toggles panel; clicking All Tools opens modal; clicking other views hides History
            const toolsOverlay = document.getElementById('toolsModalOverlay');
            const toolsCloseBtn = document.getElementById('toolsModalClose');

            function showToolsModal() {
                if (!toolsOverlay) return;
                toolsOverlay.classList.add('visible');
                toolsOverlay.setAttribute('aria-hidden','false');
            }

            function hideToolsModal() {
                if (!toolsOverlay) return;
                toolsOverlay.classList.remove('visible');
                toolsOverlay.setAttribute('aria-hidden','true');
            }

            navItems.forEach(item => {
                item.addEventListener('click', () => {
                    const view = item.dataset.view || 'home';

                    if (view === 'history') {
                        const isHidden = appContainer.classList.contains('no-history');
                        if (isHidden) {
                            appContainer.classList.remove('no-history');
                            setActiveByName('history');
                        } else {
                            appContainer.classList.add('no-history');
                            setActiveByName('home');
                        }
                        // ensure modal closed when switching
                        hideToolsModal();
                    } else if (view === 'tools') {
                        // Toggle modal when clicking All Tools
                        const isOpen = toolsOverlay && toolsOverlay.classList.contains('visible');
                        if (isOpen) {
                            hideToolsModal();
                            // keep All Tools active state toggled off when closing
                            setActiveByName('home');
                        } else {
                            showToolsModal();
                            setActiveByName('tools');
                        }
                        // hide history while modal is up
                        appContainer.classList.add('no-history');
                    } else {
                        appContainer.classList.add('no-history');
                        setActiveByName(view);
                        hideToolsModal();
                    }
                });
            });

            // Close modal on overlay click or on close button
            if (toolsOverlay) {
                toolsOverlay.addEventListener('click', (e) => {
                    if (e.target === toolsOverlay) hideToolsModal();
                });
            }
            if (toolsCloseBtn) {
                toolsCloseBtn.addEventListener('click', hideToolsModal);
            }

            // Initialize: Home active and history hidden
            appContainer.classList.add('no-history');
            setActiveByName('home');
        })();
    </script>
</body>
</html>