<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

                        <!DOCTYPE html>
                        <html lang="vi">

                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Dashboard - ConvertFile</title>

                            <script>
                                // Xóa cookie g_state bị lỗi nếu có
                                document.cookie = "g_state=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
                            </script>

                            <!-- Fonts -->
                            <link rel="preconnect" href="https://fonts.googleapis.com">
                            <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                            <link
                                href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700;800&display=swap"
                                rel="stylesheet">

                            <style>
                                :root {
                                    --primary: #6366f1;
                                    --primary-dark: #4f46e5;
                                    --secondary: #ec4899;
                                    --accent: #8b5cf6;
                                    --text-main: #1e293b;
                                    --text-light: #64748b;
                                    --bg-glass: rgba(255, 255, 255, 0.7);
                                    --bg-glass-strong: rgba(255, 255, 255, 0.9);
                                    --shadow-glass: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
                                    --border-glass: 1px solid rgba(255, 255, 255, 0.18);
                                    --success: #10b981;
                                    --warning: #f59e0b;
                                    --error: #ef4444;
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
            overflow-x: hidden;
            overflow-y: auto;
            position: relative;
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
                                    opacity: 0.5;
                                    animation: float 20s infinite ease-in-out;
                                }

                                .orb-1 {
                                    width: 500px;
                                    height: 500px;
                                    background: var(--primary);
                                    top: -10%;
                                    left: -10%;
                                    animation-delay: 0s;
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

                                    33% {
                                        transform: translate(30px, -50px) scale(1.1);
                                    }

                                    66% {
                                        transform: translate(-20px, 20px) scale(0.9);
                                    }
                                }

        /* Main Layout */
        .app-layout {
            display: grid;
            grid-template-columns: 260px 1fr;
            min-height: 100vh;
            width: 100vw;
            gap: 20px;
            padding: 20px;
            transition: grid-template-columns 0.3s ease;
        }

                                .app-layout.show-history {
                                    grid-template-columns: 260px 1fr 320px;
                                }

                                /* Glass Panel Common */
                                .glass-panel {
                                    background: var(--bg-glass);
                                    backdrop-filter: blur(20px);
                                    -webkit-backdrop-filter: blur(20px);
                                    border-radius: 24px;
                                    border: var(--border-glass);
                                    box-shadow: var(--shadow-glass);
                                    display: flex;
                                    flex-direction: column;
                                    overflow: hidden;
                                }

                                /* Sidebar */
                                .sidebar {
                                    padding: 24px;
                                }

                                .brand {
                                    font-size: 24px;
                                    font-weight: 800;
                                    background: linear-gradient(135deg, var(--primary), var(--secondary));
                                    -webkit-background-clip: text;
                                    -webkit-text-fill-color: transparent;
                                    margin-bottom: 40px;
                                    display: flex;
                                    align-items: center;
                                    gap: 10px;
                                }

                                .nav-menu {
                                    flex: 1;
                                    display: flex;
                                    flex-direction: column;
                                    gap: 8px;
                                }

                                .nav-item {
                                    display: flex;
                                    align-items: center;
                                    gap: 12px;
                                    padding: 12px 16px;
                                    border-radius: 12px;
                                    color: var(--text-light);
                                    text-decoration: none;
                                    font-weight: 500;
                                    transition: all 0.3s ease;
                                    cursor: pointer;
                                }

                                .nav-item:hover,
                                .nav-item.active {
                                    background: rgba(255, 255, 255, 0.5);
                                    color: var(--primary);
                                    transform: translateX(5px);
                                }

                                .nav-item.active {
                                    background: white;
                                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
                                }

                                .user-profile {
                                    margin-top: auto;
                                    padding-top: 20px;
                                    border-top: 1px solid rgba(0, 0, 0, 0.05);
                                    display: flex;
                                    align-items: center;
                                    gap: 12px;
                                }

                                .avatar {
                                    width: 40px;
                                    height: 40px;
                                    border-radius: 50%;
                                    background: linear-gradient(135deg, var(--primary), var(--accent));
                                    display: flex;
                                    align-items: center;
                                    justify-content: center;
                                    color: white;
                                    font-weight: 700;
                                    font-size: 16px;
                                }

                                .user-info h4 {
                                    font-size: 14px;
                                    color: var(--text-main);
                                }

                                .user-info p {
                                    font-size: 12px;
                                    color: var(--text-light);
                                }

        /* Main Content */
        .main-content {
            padding: 32px;
            overflow-x: hidden;
        }

                                .header {
                                    margin-bottom: 32px;
                                }

                                .header h1 {
                                    font-size: 28px;
                                    color: var(--text-main);
                                    margin-bottom: 8px;
                                }

                                .header p {
                                    color: var(--text-light);
                                }

        /* Upload Area */
        .upload-zone {
            background: rgba(255, 255, 255, 0.5);
            border: 2px dashed #cbd5e1;
            border-radius: 20px;
            padding: 50px 40px;
            height: 320px;
            text-align: center;
            transition: all 0.3s ease;
            cursor: pointer;
            margin-bottom: 32px;
            position: relative;
            overflow: visible;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;
        }

                                .upload-zone:hover,
                                .upload-zone.dragover {
                                    border-color: var(--primary);
                                    background: rgba(99, 102, 241, 0.05);
                                    transform: scale(1.01);
                                }

        .upload-icon {
            width: 90px;
            height: 90px;
            background: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 24px;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
            color: var(--primary);
            font-size: 36px;
            flex-shrink: 0;
        }

        .upload-title {
            font-size: 20px;
            font-weight: 600;
            color: var(--text-main);
            margin-bottom: 12px;
        }

        .upload-desc {
            color: var(--text-light);
            font-size: 15px;
            margin-bottom: 28px;
        }

        .browse-btn {
            padding: 14px 40px;
            background: linear-gradient(135deg, var(--primary), var(--accent));
            color: white;
            border: none;
            border-radius: 12px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
            margin-top: 8px;
        }

        .browse-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(99, 102, 241, 0.4);
        }
        
        .browse-btn:active {
            transform: translateY(0);
        }

        /* Selected Files */
        #selectedFilesSection {
            margin-bottom: 40px;
        }

        .section-title {
            font-size: 18px;
            font-weight: 600;
            color: var(--text-main);
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .file-list {
            display: flex;
            flex-direction: column;
            gap: 14px;
            margin-bottom: 24px;
            max-height: 400px;
            overflow-y: auto;
            padding-right: 8px;
        }

        .file-list::-webkit-scrollbar {
            width: 6px;
        }

        .file-list::-webkit-scrollbar-track {
            background: rgba(0, 0, 0, 0.05);
            border-radius: 10px;
        }

        .file-list::-webkit-scrollbar-thumb {
            background: var(--primary);
            border-radius: 10px;
        }

                                .file-item {
                                    background: white;
                                    padding: 16px;
                                    border-radius: 16px;
                                    display: flex;
                                    align-items: center;
                                    gap: 16px;
                                    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
                                    transition: all 0.3s ease;
                                    animation: slideIn 0.3s ease;
                                }

                                .file-item:hover {
                                    transform: translateY(-2px);
                                    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.05);
                                }

                                .file-icon {
                                    width: 48px;
                                    height: 48px;
                                    background: #f1f5f9;
                                    border-radius: 12px;
                                    display: flex;
                                    align-items: center;
                                    justify-content: center;
                                    font-size: 24px;
                                }

                                .file-details {
                                    flex: 1;
                                }

                                .file-name {
                                    font-weight: 600;
                                    color: var(--text-main);
                                    font-size: 14px;
                                    margin-bottom: 4px;
                                }

                                .file-meta {
                                    font-size: 12px;
                                    color: var(--text-light);
                                    display: flex;
                                    gap: 12px;
                                }

                                .file-actions {
                                    display: flex;
                                    align-items: center;
                                    gap: 12px;
                                }

        .format-select {
            padding: 8px 12px;
            border-radius: 8px;
            border: 1px solid #e2e8f0;
            background: #f8fafc;
            font-size: 13px;
            color: var(--text-main);
            outline: none;
            cursor: pointer;
            width: 180px;
            min-width: 180px;
        }

        .convert-single-btn {
            width: 36px;
            height: 36px;
            border-radius: 8px;
            border: none;
            background: linear-gradient(135deg, var(--primary), var(--accent));
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s;
            box-shadow: 0 2px 8px rgba(99, 102, 241, 0.3);
        }

        .convert-single-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(99, 102, 241, 0.4);
        }

        .convert-single-btn:active {
            transform: translateY(0);
        }

        .convert-single-btn:disabled {
            opacity: 0.5;
            cursor: not-allowed;
            transform: none;
        }

                                .remove-btn {
                                    width: 32px;
                                    height: 32px;
                                    border-radius: 8px;
                                    border: none;
                                    background: #fee2e2;
                                    color: #ef4444;
                                    display: flex;
                                    align-items: center;
                                    justify-content: center;
                                    cursor: pointer;
                                    transition: all 0.2s;
                                }

                                .remove-btn:hover {
                                    background: #fecaca;
                                }

        /* Convert Button */
        .convert-all-btn {
            width: 100%;
            padding: 18px;
            background: linear-gradient(135deg, var(--secondary), var(--primary));
            color: white;
            border: none;
            border-radius: 16px;
            font-size: 17px;
            font-weight: 700;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 8px 20px rgba(236, 72, 153, 0.3);
            display: none;
            position: sticky;
            bottom: 0;
            z-index: 10;
            backdrop-filter: blur(10px);
        }

                                .convert-all-btn.show {
                                    display: block;
                                }

                                .convert-all-btn:hover {
                                    transform: translateY(-2px);
                                    box-shadow: 0 12px 28px rgba(236, 72, 153, 0.4);
                                }

                                .convert-all-btn:disabled {
                                    opacity: 0.7;
                                    cursor: wait;
                                }

                                /* Right Panel - History */
                                .history-panel {
                                    padding: 24px;
                                    overflow-y: auto;
                                    display: none;
                                    /* Hidden by default */
                                }

                                .history-panel.show {
                                    display: flex !important;
                                }

                                .history-list {
                                    display: flex;
                                    flex-direction: column;
                                    gap: 12px;
                                }

                                .history-item {
                                    background: rgba(255, 255, 255, 0.6);
                                    padding: 12px;
                                    border-radius: 12px;
                                    display: flex;
                                    align-items: center;
                                    gap: 12px;
                                    transition: all 0.2s;
                                }

                                .history-item:hover {
                                    background: white;
                                }

                                .status-badge {
                                    padding: 4px 8px;
                                    border-radius: 6px;
                                    font-size: 10px;
                                    font-weight: 700;
                                    text-transform: uppercase;
                                }

                                .status-pending {
                                    background: #fef3c7;
                                    color: #d97706;
                                }

                                .status-processing {
                                    background: #dbeafe;
                                    color: #2563eb;
                                }

                                .status-done {
                                    background: #d1fae5;
                                    color: #059669;
                                }

                                .status-error {
                                    background: #fee2e2;
                                    color: #dc2626;
                                }

        .download-btn {
            padding: 8px 16px;
            background: linear-gradient(135deg, var(--primary), var(--secondary));
            border: none;
            border-radius: 8px;
            color: white;
            font-size: 13px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.3s;
            box-shadow: 0 2px 8px rgba(99, 102, 241, 0.3);
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }

        .download-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(99, 102, 241, 0.4);
        }

        .download-btn:active {
            transform: translateY(0);
        }

        /* Progress Bar */
        .progress-container {
            width: 100%;
            height: 6px;
            background: #f1f5f9;
            border-radius: 10px;
            overflow: hidden;
            margin-top: 8px;
        }

        .progress-bar {
            height: 100%;
            background: linear-gradient(90deg, var(--primary), var(--secondary));
            border-radius: 10px;
            transition: width 0.3s ease;
            position: relative;
        }

        .progress-bar::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
            animation: shimmer 1.5s infinite;
        }

        @keyframes shimmer {
            0% { transform: translateX(-100%); }
            100% { transform: translateX(100%); }
        }

                                /* Animations */
                                @keyframes slideIn {
                                    from {
                                        opacity: 0;
                                        transform: translateY(10px);
                                    }

                                    to {
                                        opacity: 1;
                                        transform: translateY(0);
                                    }
                                }

                                /* Responsive */
                                @media (max-width: 1200px) {
                                    .app-layout {
                                        grid-template-columns: 240px 1fr;
                                    }

                                    .history-panel {
                                        display: none;
                                    }

                                    .history-panel.show {
                                        display: flex !important;
                                        position: fixed;
                                        top: 20px;
                                        right: 20px;
                                        bottom: 20px;
                                        width: 320px;
                                        z-index: 1000;
                                        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
                                        background: rgba(255, 255, 255, 0.95);
                                        /* More opaque for overlay */
                                    }
                                }

                                @media (max-width: 768px) {
                                    .app-layout {
                                        grid-template-columns: 1fr;
                                        padding: 10px;
                                    }

                                    .sidebar {
                                        display: none;
                                    }

                                    /* Mobile menu needed */
                                    .main-content {
                                        padding: 20px;
                                    }
                                }
                            </style>
                        </head>

                        <body>

                            <!-- Background Animation -->
                            <div class="bg-animation">
                                <div class="orb orb-1"></div>
                                <div class="orb orb-2"></div>
                                <div class="orb orb-3"></div>
                            </div>

                            <div class="app-layout">
                                <!-- Sidebar -->
                                <aside class="glass-panel sidebar">
                                    <div class="brand">
                                        <svg width="32" height="32" viewBox="0 0 24 24" fill="none"
                                            stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                            stroke-linejoin="round">
                                            <path
                                                d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z" />
                                            <polyline points="14 2 14 8 20 8" />
                                        </svg>
                                        ConvertFile
                                    </div>

                                    <nav class="nav-menu">
                                        <a href="#" class="nav-item active" id="navDashboard">
                                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                                stroke="currentColor" stroke-width="2">
                                                <rect x="3" y="3" width="7" height="7"></rect>
                                                <rect x="14" y="3" width="7" height="7"></rect>
                                                <rect x="14" y="14" width="7" height="7"></rect>
                                                <rect x="3" y="14" width="7" height="7"></rect>
                                            </svg>
                                            Dashboard
                                        </a>
                                        <a href="#" class="nav-item" id="navMyFiles">
                                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                                stroke="currentColor" stroke-width="2">
                                                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z">
                                                </path>
                                                <polyline points="14 2 14 8 20 8"></polyline>
                                                <line x1="16" y1="13" x2="8" y2="13"></line>
                                                <line x1="16" y1="17" x2="8" y2="17"></line>
                                                <polyline points="10 9 9 9 8 9"></polyline>
                                            </svg>
                                            My Files
                                        </a>
                                        <a href="#" class="nav-item">
                                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                                stroke="currentColor" stroke-width="2">
                                                <circle cx="12" cy="12" r="3"></circle>
                                                <path
                                                    d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z">
                                                </path>
                                            </svg>
                                            Settings
                                        </a>
                                    </nav>

                                    <div class="user-profile">
                                        <div class="avatar">
                                            <c:choose>
                                                <c:when test="${not empty sessionScope.userpicture}">
                                                    <img src="${sessionScope.userpicture}" alt="Avatar"
                                                        style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;">
                                                </c:when>
                                                <c:when test="${not empty sessionScope.username}">
                                                    ${sessionScope.username.substring(0, 1).toUpperCase()}
                                                </c:when>
                                                <c:otherwise>G</c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="user-info">
                                            <h4>${not empty sessionScope.username ? sessionScope.username : "Guest"}
                                            </h4>
                                            <p>${not empty sessionScope.email ? sessionScope.email : "Sign in to save"}
                                            </p>
                                        </div>
                                        <a href="<c:url value='/logout'/>" title="Logout"
                                            style="margin-left: auto; color: var(--text-light);">
                                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                                stroke="currentColor" stroke-width="2">
                                                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                                                <polyline points="16 17 21 12 16 7"></polyline>
                                                <line x1="21" y1="12" x2="9" y2="12"></line>
                                            </svg>
                                        </a>
                                    </div>
                                </aside>

                                <!-- Main Content -->
                                <main class="glass-panel main-content">
                                    <div class="header">
                                        <h1>Welcome back, ${not empty sessionScope.username ? sessionScope.username :
                                            "Guest"}! 👋</h1>
                                        <p>Ready to convert some files? Drag and drop them below.</p>
                                    </div>

            <!-- Upload Zone -->
            <div class="upload-zone" id="dropZone">
                <div class="upload-icon">
                    <svg width="40" height="40" viewBox="0 0 24 24" fill="none"
                        stroke="currentColor" stroke-width="2">
                        <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                        <polyline points="17 8 12 3 7 8"></polyline>
                        <line x1="12" y1="3" x2="12" y2="15"></line>
                    </svg>
                </div>
                <h3 class="upload-title">Drag & Drop files here</h3>
                <p class="upload-desc">or click to browse multiple files from your computer</p>
                <button class="browse-btn"
                    onclick="document.getElementById('fileInput').click()">Browse Files</button>
                <input type="file" id="fileInput" multiple accept=".pdf,.doc,.docx,.jpg,.jpeg,.png,.gif,.bmp,.csv,.xlsx,.xls,.xml,.html,.htm,.txt,.md" hidden>
            </div>

                                    <!-- Selected Files List -->
                                    <div id="selectedFilesSection" style="display: none;">
                                        <div class="section-title">
                                            <span>Selected Files</span>
                                            <span class="file-count" id="fileCount">0 files</span>
                                        </div>
                                        <div class="file-list" id="fileList">
                                            <!-- Files will be added here -->
                                        </div>
                                        <button class="convert-all-btn" id="uploadAllBtn">
                                            Convert All Files 🚀
                                        </button>
                                    </div>

                                    <!-- Recent Jobs (Mobile/Tablet view mostly, or main list) -->
                                    <div class="section-title" style="margin-top: 40px;">
                                        <span>Recent Uploads</span>
                                    </div>
                                    <div class="file-list" id="jobListMain">
                                        <!-- Jobs will be rendered here -->
                                    </div>
                                </main>

                                <!-- Right Panel (History) -->
                                <aside class="glass-panel history-panel">
                                    <div class="section-title">History</div>
                                    <div class="history-list" id="historyList">
                                        <!-- History items will be rendered here -->
                                    </div>
                                </aside>
                            </div>

    <script>
        console.log("🎬 Script started loading...");
        
        // Clean up Google g_state cookie to prevent Tomcat warnings
        document.cookie = "g_state=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";

        // --- Variables ---
        const dropZone = document.getElementById('dropZone');
        const fileInput = document.getElementById('fileInput');
        const fileList = document.getElementById('fileList');
        const selectedFilesSection = document.getElementById('selectedFilesSection');
        const uploadAllBtn = document.getElementById('uploadAllBtn');
        const fileCount = document.getElementById('fileCount');
        const historyList = document.getElementById('historyList');
        const jobListMain = document.getElementById('jobListMain');
        
        console.log("🔍 Element check:");
        console.log("  dropZone:", dropZone ? "✅" : "❌");
        console.log("  fileInput:", fileInput ? "✅" : "❌");
        console.log("  uploadAllBtn:", uploadAllBtn ? "✅" : "❌");
        console.log("  jobListMain:", jobListMain ? "✅" : "❌");

                                    // Navigation elements
                                    const navDashboard = document.getElementById('navDashboard');
                                    const navMyFiles = document.getElementById('navMyFiles');
                                    const historyPanel = document.querySelector('.history-panel');
                                    const appLayout = document.querySelector('.app-layout');

                                    let selectedFiles = []; // Array to store File objects

                                    // --- Navigation Toggle ---
                                    if (navDashboard && navMyFiles && historyPanel && appLayout) {
                                        navDashboard.addEventListener('click', function (e) {
                                            e.preventDefault();
                                            console.log("Dashboard clicked");
                                            navDashboard.classList.add('active');
                                            navMyFiles.classList.remove('active');
                                            historyPanel.classList.remove('show');
                                            appLayout.classList.remove('show-history');
                                            historyPanel.style.display = '';
                                        });

                                        navMyFiles.addEventListener('click', function (e) {
                                            e.preventDefault();
                                            console.log("My Files clicked - Toggling History Panel");
                                            navMyFiles.classList.add('active');
                                            navDashboard.classList.remove('active');
                                            historyPanel.classList.add('show');
                                            appLayout.classList.add('show-history');

                                            // Force display flex if class doesn't work for some reason
                                            historyPanel.style.display = 'flex';
                                        });
                                    } else {
                                        console.error("One or more navigation elements not found:", {
                                            navDashboard: !!navDashboard,
                                            navMyFiles: !!navMyFiles,
                                            historyPanel: !!historyPanel,
                                            appLayout: !!appLayout
                                        });
                                    }

                                    // --- Drag & Drop Logic ---
                                    if (dropZone) {
                                        ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
                                            dropZone.addEventListener(eventName, preventDefaults, false);
                                        });

                                        function preventDefaults(e) {
                                            e.preventDefault();
                                            e.stopPropagation();
                                        }

                                        ['dragenter', 'dragover'].forEach(eventName => {
                                            dropZone.addEventListener(eventName, () => dropZone.classList.add('dragover'), false);
                                        });

                                        ['dragleave', 'drop'].forEach(eventName => {
                                            dropZone.addEventListener(eventName, () => dropZone.classList.remove('dragover'), false);
                                        });

                                        dropZone.addEventListener('drop', handleDrop, false);
                                    }

        function handleDrop(e) {
            console.log("📥 Drop event triggered");
            const dt = e.dataTransfer;
            const files = dt.files;
            console.log("📦 Dropped files count:", files.length);
            handleFiles(files);
        }

        fileInput.addEventListener('change', function () {
            console.log("📁 File input changed");
            console.log("📦 Selected files count:", this.files.length);
            handleFiles(this.files);
        });

        function handleFiles(files) {
            console.log("🔄 handleFiles called with", files.length, "files");
            if (!files.length) {
                console.warn("⚠️ No files to handle");
                return;
            }

            // Convert FileList to Array and add to selectedFiles
            const newFiles = Array.from(files);
            selectedFiles = [...selectedFiles, ...newFiles];
            console.log("✅ Total selected files:", selectedFiles.length);

            renderSelectedFiles();
        }

        // --- Conversion Options Map ---
        const conversionOptions = {
            'pdf': [
                { value: 'PDF_TO_DOCX', text: 'PDF → Word' },
                { value: 'PDF_TO_IMAGE', text: 'PDF → Image' }
            ],
            'doc': [
                { value: 'DOCX_TO_PDF', text: 'Word → PDF' },
                { value: 'DOCX_TO_HTML', text: 'Word → HTML' },
                { value: 'DOCX_TO_TXT', text: 'Word → TXT' },
                { value: 'DOCX_TO_XML', text: 'Word → XML' },
                { value: 'DOCX_TO_MARKDOWN', text: 'Word → Markdown' }
            ],
            'docx': [
                { value: 'DOCX_TO_PDF', text: 'Word → PDF' },
                { value: 'DOCX_TO_HTML', text: 'Word → HTML' },
                { value: 'DOCX_TO_TXT', text: 'Word → TXT' },
                { value: 'DOCX_TO_XML', text: 'Word → XML' },
                { value: 'DOCX_TO_MARKDOWN', text: 'Word → Markdown' }
            ],
            'csv': [
                { value: 'CSV_TO_JSON', text: 'CSV → JSON' }
            ],
            'xlsx': [
                { value: 'XLSX_TO_CSV', text: 'Excel → CSV' }
            ],
            'xls': [
                { value: 'XLSX_TO_CSV', text: 'Excel → CSV' }
            ],
            'jpg': [
                { value: 'IMAGE_TO_PDF', text: 'Image → PDF' },
                { value: 'IMG_FORMAT', text: 'Image Format' }
            ],
            'jpeg': [
                { value: 'IMAGE_TO_PDF', text: 'Image → PDF' },
                { value: 'IMG_FORMAT', text: 'Image Format' }
            ],
            'png': [
                { value: 'IMAGE_TO_PDF', text: 'Image → PDF' },
                { value: 'IMG_FORMAT', text: 'Image Format' }
            ],
            'gif': [
                { value: 'IMAGE_TO_PDF', text: 'Image → PDF' },
                { value: 'IMG_FORMAT', text: 'Image Format' }
            ],
            'bmp': [
                { value: 'IMAGE_TO_PDF', text: 'Image → PDF' },
                { value: 'IMG_FORMAT', text: 'Image Format' }
            ],
            'html': [
                { value: 'HTML_TO_MARKDOWN', text: 'HTML → Markdown' }
            ],
            'md': [
                { value: 'MARKDOWN_TO_HTML', text: 'Markdown → HTML' }
            ],
            'markdown': [
                { value: 'MARKDOWN_TO_HTML', text: 'Markdown → HTML' }
            ],
            'pptx': [
                { value: 'PPTX_TO_PDF', text: 'PowerPoint → PDF' }
            ],
            'ppt': [
                { value: 'PPTX_TO_PDF', text: 'PowerPoint → PDF' }
            ],
            'xml': [
                { value: 'XML_TO_DOCX', text: 'XML → Word' }
            ]
        };

        // Get conversion options for file extension
        function getConversionOptions(fileName) {
            const extension = fileName.split('.').pop().toLowerCase();
            return conversionOptions[extension] || [];
        }

                                    // --- Render Selected Files ---
                                    function renderSelectedFiles() {
                                        fileList.innerHTML = '';

                                        if (selectedFiles.length > 0) {
                                            selectedFilesSection.style.display = 'block';
                                            uploadAllBtn.classList.add('show');
                                            fileCount.textContent = selectedFiles.length + ' files';
                                        } else {
                                            selectedFilesSection.style.display = 'none';
                                            uploadAllBtn.classList.remove('show');
                                        }

            selectedFiles.forEach((file, index) => {
                const item = document.createElement('div');
                item.className = 'file-item';
                item.dataset.fileIndex = index; // Store index in data attribute

                // Determine icon based on type
                let icon = '📄';
                if (file.type.includes('pdf')) icon = '📕';
                else if (file.type.includes('image')) icon = '🖼️';
                else if (file.type.includes('word') || file.name.endsWith('.docx') || file.name.endsWith('.doc')) icon = '📝';
                else if (file.name.endsWith('.csv')) icon = '📊';
                else if (file.name.endsWith('.xlsx') || file.name.endsWith('.xls')) icon = '📊';
                else if (file.name.endsWith('.xml')) icon = '📋';
                else if (file.name.endsWith('.html') || file.name.endsWith('.htm')) icon = '🌐';
                else if (file.name.endsWith('.txt')) icon = '📃';
                else if (file.name.endsWith('.md')) icon = '📝';

                                            const size = (file.size / 1024 / 1024).toFixed(2) + ' MB';

                let htmlContent = '<div class="file-icon">' + icon + '</div>' +
                    '<div class="file-details">' +
                    '<div class="file-name">' + file.name + '</div>' +
                    '<div class="file-meta">' + size + '</div>' +
                    '</div>' +
                    '<div class="file-actions">' +
                    '<select class="format-select" data-file-index="' + index + '">';
                
                // Get conversion options for this file type
                const options = getConversionOptions(file.name);
                if (options.length > 0) {
                    options.forEach(opt => {
                        htmlContent += '<option value="' + opt.value + '">' + opt.text + '</option>';
                    });
                } else {
                    htmlContent += '<option value="">No conversion available</option>';
                }
                
                htmlContent += '</select>' +
                    '<button class="convert-single-btn" data-file-index="' + index + '" title="Convert this file">' +
                    '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">' +
                    '<polyline points="16 3 21 3 21 8"></polyline>' +
                    '<line x1="4" y1="20" x2="21" y2="3"></line>' +
                    '<polyline points="21 16 21 21 16 21"></polyline>' +
                    '<line x1="15" y1="15" x2="21" y2="21"></line>' +
                    '<line x1="4" y1="4" x2="9" y2="9"></line>' +
                    '</svg>' +
                    '</button>' +
                    '<button class="remove-btn" onclick="removeFile(' + index + ')">' +
                    '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>' +
                    '</button>' +
                    '</div>';
                
                item.innerHTML = htmlContent;
                fileList.appendChild(item);
            });
        }

        window.removeFile = function (index) {
            selectedFiles.splice(index, 1);
            renderSelectedFiles();
        };

        // --- Single File Upload ---
        async function uploadSingleFile(index) {
            const file = selectedFiles[index];
            const selectElement = document.querySelector('.format-select[data-file-index="' + index + '"]');
            const convertBtn = document.querySelector('.convert-single-btn[data-file-index="' + index + '"]');
            
            if (!file || !selectElement || !convertBtn) {
                console.error('File, select or button not found for index:', index);
                return;
            }

            const taskType = selectElement.value;
            convertBtn.disabled = true;
            convertBtn.innerHTML = '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle></svg>';
            
            console.log('🚀 Uploading single file:', file.name, '-> TaskType:', taskType);

            const formData = new FormData();
            formData.append('file', file);
            formData.append('taskType', taskType);

            try {
                const response = await fetch('upload', {
                    method: 'POST',
                    body: formData
                });

                if (!response.ok) {
                    throw new Error('HTTP Error: ' + response.status);
                }

                const data = await response.json();
                
                if (data.status === 'error') {
                    console.error('❌ Upload failed:', data.message);
                    alert('Upload failed: ' + data.message);
                    convertBtn.disabled = false;
                    convertBtn.innerHTML = '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="16 3 21 3 21 8"></polyline><line x1="4" y1="20" x2="21" y2="3"></line><polyline points="21 16 21 21 16 21"></polyline><line x1="15" y1="15" x2="21" y2="21"></line><line x1="4" y1="4" x2="9" y2="9"></line></svg>';
                } else {
                    console.log('✅ Upload successful:', file.name);
                    // Remove file from selected list
                    selectedFiles.splice(index, 1);
                    renderSelectedFiles();
                    // Refresh job list
                    fetchJobs();
                }
            } catch (error) {
                console.error('❌ Exception:', error);
                alert('Upload failed: ' + error.message);
                convertBtn.disabled = false;
                convertBtn.innerHTML = '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="16 3 21 3 21 8"></polyline><line x1="4" y1="20" x2="21" y2="3"></line><polyline points="21 16 21 21 16 21"></polyline><line x1="15" y1="15" x2="21" y2="21"></line><line x1="4" y1="4" x2="9" y2="9"></line></svg>';
            }
        }

        // Attach event listeners to convert buttons
        document.addEventListener('click', function(e) {
            if (e.target.closest('.convert-single-btn')) {
                const btn = e.target.closest('.convert-single-btn');
                const index = parseInt(btn.dataset.fileIndex);
                uploadSingleFile(index);
            }
        });

        // --- Upload Logic ---
        console.log("📌 Attaching click listener to uploadAllBtn...");
        
        if (!uploadAllBtn) {
            console.error("❌ FATAL: uploadAllBtn not found!");
        } else {
            uploadAllBtn.addEventListener('click', async () => {
                console.log("🚀 Bắt đầu quá trình Upload..."); 
                console.log("📂 Số file được chọn:", selectedFiles.length);

            if (selectedFiles.length === 0) {
                console.log("⚠️ Không có file nào để upload.");
                return;
            }

                                        uploadAllBtn.disabled = true;
                                        uploadAllBtn.textContent = 'Uploading & Converting... ⏳';

            const uploadPromises = selectedFiles.map((file, index) => {
                // Tìm select element bằng data-file-index
                const selectElement = document.querySelector('.format-select[data-file-index="' + index + '"]');
                
                let taskType = "PDF_TO_DOCX"; // Giá trị mặc định
                
                if (selectElement) {
                    taskType = selectElement.value;
                    console.log('📋 File ' + index + ': ' + file.name + ' -> TaskType: ' + taskType);
                } else {
                    console.warn('⚠️ Không tìm thấy select box cho file index ' + index + ' (' + file.name + '), dùng mặc định: ' + taskType);
                }

                console.log('📤 [' + (index + 1) + '/' + selectedFiles.length + '] Đang gửi: ' + file.name + ' (' + (file.size / 1024).toFixed(2) + ' KB)');

                const formData = new FormData();
                formData.append('file', file);
                formData.append('taskType', taskType);

                return fetch('upload', {
                    method: 'POST',
                    body: formData
                })
                .then(response => {
                    console.log('✅ Response từ server cho ' + file.name + ': Status ' + response.status);
                    if (!response.ok) {
                         throw new Error('HTTP Error: ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('📦 Response data cho ' + file.name + ':', data);
                    if (data.status === 'error') {
                        console.error('❌ Backend error cho ' + file.name + ':', data.message);
                        return { success: false, file: file.name, error: data.message };
                    }
                    console.log('✨ Upload thành công: ' + file.name);
                    return { success: true, file: file.name };
                })
                .catch(error => {
                    console.error('❌ Exception khi upload ' + file.name + ':', error);
                    return { success: false, file: file.name, error: error.toString() };
                });
            });

            try {
                const results = await Promise.all(uploadPromises);
                console.log("🏁 Kết quả cuối cùng:", results);

                const errors = results.filter(r => !r.success);
                const successes = results.filter(r => r.success);

                console.log('✅ Thành công: ' + successes.length + '/' + selectedFiles.length);
                console.log('❌ Lỗi: ' + errors.length + '/' + selectedFiles.length);

                if (errors.length > 0) {
                    console.error('Một số file gặp lỗi:', errors);
                } else {
                    console.log("🎉 Tất cả file đã upload thành công!");
                }

                // Clear selected files và refresh UI
                selectedFiles = [];
                renderSelectedFiles();
                
                // Refresh job list
                console.log("🔄 Đang tải lại danh sách jobs...");
                fetchJobs(); 

            } catch (err) {
                console.error('💥 Lỗi nghiêm trọng trong quá trình upload:', err);
            } finally {
                uploadAllBtn.disabled = false;
                uploadAllBtn.textContent = 'Convert All Files 🚀';
                console.log("🔚 Hoàn tất quá trình upload.");
            }
        });
        console.log("✅ Upload listener attached successfully!");
    }

        // --- Job Polling Logic ---
        function fetchJobs() {
            console.log("🔄 Fetching jobs from server...");
            fetch('jobs', {
                method: 'GET',
                credentials: 'same-origin' // ✅ Send cookies with request
            })
                .then(response => {
                    console.log("📡 Jobs response status:", response.status);
                    if (!response.ok) {
                        throw new Error('HTTP Error: ' + response.status);
                    }
                    return response.json();
                })
                .then(jobs => {
                    console.log("📋 Jobs received:", jobs.length, "items");
                    console.log("Jobs data:", jobs);
                    renderJobs(jobs);

                    // If any job is still processing, poll again
                    const hasProcessing = jobs.some(job => job.status === 'PENDING' || job.status === 'IN_PROGRESS' || job.status === 'WAITING' || job.status === 'PROCESSING');
                    if (hasProcessing) {
                        console.log("⏳ Has processing jobs, will poll again in 2s");
                        setTimeout(fetchJobs, 2000); // Poll every 2s
                    }
                })
                .catch(err => {
                    console.error('❌ Error fetching jobs:', err);
                });
        }

                                    function renderJobs(jobs) {
                                        // Render Main List (Detailed)
                                        jobListMain.innerHTML = '';
                                        // Render History List (Compact)
                                        historyList.innerHTML = '';

                                        if (jobs.length === 0) {
                                            jobListMain.innerHTML = '<div style="text-align:center; color:var(--text-light); padding:20px;">No recent conversions found.</div>';
                                            historyList.innerHTML = '<div style="text-align:center; color:var(--text-light); padding:20px; font-size: 14px;">No history yet.</div>';
                                            return;
                                        }

                                        jobs.forEach(job => {
                                            // --- Main List Item ---
                                            const mainItem = document.createElement('div');
                                            mainItem.className = 'file-item';

                                            let statusClass = 'status-pending';
                                            let statusText = job.status;
                                            let actionHtml = '';

                if (job.status === 'COMPLETED' || job.status === 'DONE') {
                    statusClass = 'status-done';
                    statusText = 'COMPLETED';
                    actionHtml = '<a href="download?fileId=' + encodeURIComponent(job.fileId) + '" class="download-btn" style="display:inline-block;">📥 Download</a>';
                } else if (job.status === 'FAILED' || job.status === 'ERROR') {
                    statusClass = 'status-error';
                    statusText = 'FAILED';
                    actionHtml = '<span style="color:var(--error); font-size:12px;">❌ Failed</span>';
                } else if (job.status === 'WAITING' || job.status === 'PENDING') {
                    statusClass = 'status-pending';
                    statusText = 'WAITING';
                    actionHtml = '<span style="color:var(--warning); font-size:12px;">⏳ Waiting...</span>';
                } else if (job.status === 'PROCESSING' || job.status === 'IN_PROGRESS') {
                    statusClass = 'status-processing';
                    statusText = 'PROCESSING';
                    actionHtml = '<span style="color:var(--primary); font-size:12px;">⚙️ ' + job.progress + '%</span>';
                } else {
                    statusClass = 'status-processing';
                    actionHtml = '<span style="color:var(--primary); font-size:12px;">⏳ ' + job.progress + '%</span>';
                }

                // Add progress bar for processing jobs
                let progressHtml = '';
                if (job.status !== 'COMPLETED' && job.status !== 'FAILED' && job.status !== 'DONE' && job.status !== 'ERROR') {
                    progressHtml = '<div class="progress-container">' +
                        '<div class="progress-bar" style="width: ' + job.progress + '%"></div>' +
                        '</div>';
                }

                mainItem.innerHTML = '<div class="file-icon">📄</div>' +
                    '<div class="file-details">' +
                    '<div class="file-name">' + job.name + '</div>' +
                    '<div class="file-meta">' +
                    '<span class="status-badge ' + statusClass + '">' + statusText + '</span>' +
                    '<span>' + job.type + '</span>' +
                    '</div>' +
                    progressHtml +
                    '</div>' +
                    '<div class="file-actions">' +
                    actionHtml +
                    '</div>';
                jobListMain.appendChild(mainItem);

                // --- History List Item ---
                const historyItem = document.createElement('div');
                historyItem.className = 'history-item';
                historyItem.innerHTML = '<div class="file-icon" style="width:32px; height:32px; font-size:16px;">📄</div>' +
                    '<div class="file-details">' +
                    '<div class="file-name" style="font-size:13px;">' + job.name + '</div>' +
                    '<span class="status-badge ' + statusClass + '" style="font-size:9px;">' + statusText + '</span>' +
                    '</div>';
                historyList.appendChild(historyItem);
            });
        }

        // Initial load
        console.log("🔄 Initial fetchJobs...");
        fetchJobs();
        
        // Poll periodically even if idle to catch updates
        setInterval(fetchJobs, 5000);
        
        console.log("✅ Script loaded successfully!");

        // Fetch and display version info
        fetch('version')
            .then(response => response.json())
            .then(data => {
                console.log('📦 App Version:', data.version);
                console.log('🔨 Build Time:', data.buildTime);
                console.log('🚀 Deploy Time:', data.deployTime);
            })
            .catch(error => console.error('Version fetch error:', error));

    </script>
</body>

                        </html>