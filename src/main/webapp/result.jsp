<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kết quả xử lý file - CONVERT_FILE</title>
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
            max-width: 600px;
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

        .logo {
            text-align: center;
            margin-bottom: 30px;
        }

        .logo-icon {
            font-size: 60px;
            margin-bottom: 10px;
            animation: bounce 1s ease infinite;
        }

        @keyframes bounce {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-10px); }
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

        .status-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 25px;
            border-radius: 16px;
            margin-bottom: 30px;
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
            animation: fadeIn 0.6s ease 0.2s both;
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: scale(0.95);
            }
            to {
                opacity: 1;
                transform: scale(1);
            }
        }

        .status-title {
            font-size: 14px;
            opacity: 0.9;
            margin-bottom: 8px;
            font-weight: 500;
        }

        .status-value {
            font-size: 24px;
            font-weight: 700;
            text-transform: uppercase;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 30px;
            animation: fadeIn 0.6s ease 0.4s both;
        }

        .stat-card {
            background: #f5f7fa;
            padding: 25px;
            border-radius: 16px;
            border: 2px solid #e0e0e0;
            transition: all 0.3s ease;
        }

        .stat-card:hover {
            border-color: #667eea;
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.2);
        }

        .stat-icon {
            font-size: 32px;
            margin-bottom: 10px;
        }

        .stat-label {
            font-size: 13px;
            color: #666;
            margin-bottom: 5px;
            font-weight: 500;
        }

        .stat-value {
            font-size: 28px;
            color: #333;
            font-weight: 700;
        }

        .progress-bar {
            background: #e0e0e0;
            height: 8px;
            border-radius: 4px;
            overflow: hidden;
            margin-top: 15px;
        }

        .progress-fill {
            height: 100%;
            background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
            border-radius: 4px;
            transition: width 1s ease;
            animation: progress 1.5s ease-out;
        }

        @keyframes progress {
            from { width: 0%; }
        }

        .download-section {
            background: #e8f5e9;
            border: 2px solid #4caf50;
            padding: 25px;
            border-radius: 16px;
            margin-bottom: 30px;
            animation: fadeIn 0.6s ease 0.6s both;
        }

        .download-icon {
            font-size: 40px;
            text-align: center;
            margin-bottom: 15px;
        }

        .download-btn {
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
            text-decoration: none;
            display: block;
            text-align: center;
        }

        .download-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(76, 175, 80, 0.6);
        }

        .download-btn:active {
            transform: translateY(0);
        }

        .back-btn {
            width: 100%;
            padding: 16px;
            background: white;
            color: #667eea;
            border: 2px solid #667eea;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            animation: fadeIn 0.6s ease 0.8s both;
        }

        .back-btn:hover {
            background: #667eea;
            color: white;
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
        }

        .empty-state {
            text-align: center;
            padding: 40px 20px;
            color: #999;
            animation: fadeIn 0.6s ease 0.6s both;
        }

        .empty-state-icon {
            font-size: 60px;
            margin-bottom: 15px;
            opacity: 0.5;
        }

        @media (max-width: 600px) {
            .container {
                padding: 30px 20px;
            }

            h2 {
                font-size: 24px;
            }

            .stats-grid {
                grid-template-columns: 1fr;
            }

            .stat-value {
                font-size: 24px;
            }

            .status-value {
                font-size: 20px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="logo">
            <div class="logo-icon">✅</div>
            <h2>Kết quả xử lý file</h2>
            <p class="subtitle">Quá trình chuyển đổi đã hoàn tất</p>
        </div>

        <div class="status-card">
            <div class="status-title">Trạng thái tác vụ</div>
            <div class="status-value">${status}</div>
        </div>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon">📁</div>
                <div class="stat-label">Tổng số file</div>
                <div class="stat-value">${totalFiles}</div>
            </div>

            <div class="stat-card">
                <div class="stat-icon">✓</div>
                <div class="stat-label">Đã hoàn thành</div>
                <div class="stat-value">${completedFiles}</div>
                <c:if test="${totalFiles > 0}">
                    <c:set var="progressPercent" value="${completedFiles * 100 / totalFiles}" />
                    <div class="progress-bar">
                        <div class="progress-fill" data-progress="${progressPercent}"></div>
                    </div>
                </c:if>
            </div>
        </div>

        <c:if test="${not empty downloadLink}">
            <div class="download-section">
                <div class="download-icon">📥</div>
                <a href="${downloadLink}" class="download-btn">
                    ⬇ Tải xuống kết quả
                </a>
            </div>
        </c:if>

        <c:if test="${empty downloadLink}">
            <div class="empty-state">
                <div class="empty-state-icon">📂</div>
                <p>Không có file nào để tải xuống</p>
            </div>
        </c:if>

        <a href="index.jsp" class="back-btn">
            ⬅ Quay lại trang chính
        </a>
    </div>

    <script>
        // Auto-calculate progress on page load
        document.addEventListener('DOMContentLoaded', function() {
            const progressBars = document.querySelectorAll('.progress-fill');
            progressBars.forEach(bar => {
                const progress = bar.getAttribute('data-progress');
                if (progress) {
                    bar.style.width = '0%';
                    setTimeout(() => {
                        bar.style.width = progress + '%';
                    }, 100);
                }
            });
        });

        // Add pulse animation to logo icon if status is successful
        const status = '${status}'.toLowerCase();
        const logoIcon = document.querySelector('.logo-icon');
        if (status.includes('thành công') || status.includes('success') || status.includes('hoàn thành')) {
            logoIcon.style.animation = 'bounce 1s ease infinite';
        }
    </script>
</body>
</html>