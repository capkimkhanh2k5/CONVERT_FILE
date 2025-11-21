<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Process Status - ConvertFile</title>

    <script>
        // Xóa cookie g_state bị lỗi nếu có
        document.cookie = "g_state=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    </script>s

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
            color: var(--text-main);
            overflow-x: hidden;
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

        .btn-new {
            background: linear-gradient(135deg, var(--primary), var(--accent));
            color: white;
            padding: 10px 24px;
            border-radius: 12px;
            text-decoration: none;
            font-weight: 600;
            font-size: 14px;
            box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
            transition: all 0.3s ease;
        }

        .btn-new:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(99, 102, 241, 0.4);
        }

        /* Main Content */
        .main-content {
            padding: 40px 0;
        }

        .glass-card {
            background: var(--bg-glass);
            backdrop-filter: blur(20px);
            border-radius: 24px;
            border: var(--border-glass);
            box-shadow: var(--shadow-glass);
            padding: 32px;
            margin-top: 20px;
        }

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 32px;
        }

        .page-title {
            font-size: 24px;
            font-weight: 700;
            color: var(--text-main);
        }

        .status-indicator {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            font-weight: 600;
            padding: 6px 12px;
            border-radius: 20px;
            transition: all 0.3s ease;
        }

        .status-indicator.live {
            background: #e0e7ff;
            color: var(--primary);
        }

        .status-indicator.done {
            background: #d1fae5;
            color: var(--success);
        }

        .spinner {
            width: 16px;
            height: 16px;
            border: 2px solid currentColor;
            border-right-color: transparent;
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }

        @keyframes spin {
            to {
                transform: rotate(360deg);
            }
        }

        /* Job List */
        .job-list {
            display: flex;
            flex-direction: column;
            gap: 16px;
        }

        .job-item {
            background: white;
            padding: 20px;
            border-radius: 16px;
            display: flex;
            align-items: center;
            gap: 20px;
            transition: all 0.3s ease;
            border: 1px solid transparent;
        }

        .job-item:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
            border-color: rgba(99, 102, 241, 0.1);
        }

        .job-icon {
            width: 48px;
            height: 48px;
            background: #f8fafc;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
        }

        .job-info {
            flex: 1;
        }

        .job-name {
            font-weight: 600;
            color: var(--text-main);
            margin-bottom: 4px;
        }

        .job-type {
            font-size: 12px;
            color: var(--text-light);
            background: #f1f5f9;
            padding: 2px 8px;
            border-radius: 4px;
        }

        .job-status {
            width: 140px;
            text-align: right;
        }

        .badge {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 8px;
            font-size: 12px;
            font-weight: 700;
            text-transform: uppercase;
        }

        .badge.waiting {
            background: #fef3c7;
            color: var(--warning);
        }

        .badge.processing {
            background: #e0e7ff;
            color: var(--primary);
        }

        .badge.completed {
            background: #d1fae5;
            color: var(--success);
        }

        .badge.error {
            background: #fee2e2;
            color: var(--error);
        }

        /* Progress Bar */
        .progress-wrapper {
            width: 200px;
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .progress-track {
            width: 100%;
            height: 6px;
            background: #f1f5f9;
            border-radius: 10px;
            overflow: hidden;
        }

        .progress-fill {
            height: 100%;
            background: var(--primary);
            border-radius: 10px;
            transition: width 0.5s ease;
        }

        .progress-text {
            font-size: 12px;
            color: var(--text-light);
            text-align: right;
            font-weight: 600;
        }

        /* Empty State */
        .empty-state {
            text-align: center;
            padding: 40px;
            color: var(--text-light);
        }

        @media (max-width: 768px) {
            .job-item {
                flex-direction: column;
                align-items: flex-start;
                gap: 12px;
            }

            .progress-wrapper {
                width: 100%;
            }

            .job-status {
                width: 100%;
                text-align: left;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
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
                <a href="<c:url value='/home'/>" class="btn-new">Create New Task</a>
            </nav>
        </div>
    </header>

    <div class="container main-content">
        <div class="glass-card">
            <div class="page-header">
                <h1 class="page-title">Processing Tasks</h1>
                <div id="statusIndicator" class="status-indicator done">
                    All Completed
                </div>
            </div>

            <div class="job-list" id="jobListContainer">
                <!-- Jobs will be loaded here via JS -->
                <div class="empty-state">
                    <div class="spinner"
                        style="margin: 0 auto 10px; width: 30px; height: 30px; border-width: 3px; color: var(--primary);">
                    </div>
                    <p>Loading tasks...</p>
                </div>
            </div>
        </div>
    </div>

    <script>
        const jobListContainer = document.getElementById('jobListContainer');
        const statusIndicator = document.getElementById('statusIndicator');

        function fetchJobs() {
            fetch('jobs')
                .then(response => response.json())
                .then(jobs => {
                    renderJobs(jobs);

                    // Check if we need to keep polling
                    const hasProcessing = jobs.some(job => job.status === 'PENDING' || job.status === 'IN_PROGRESS' || job.status === 'WAITING' || job.status === 'PROCESSING');

                    if (hasProcessing) {
                        statusIndicator.className = 'status-indicator live';
                        statusIndicator.innerHTML = '<div class="spinner"></div> Updating...';
                        setTimeout(fetchJobs, 2000); // Poll every 2s
                    } else {
                        statusIndicator.className = 'status-indicator done';
                        statusIndicator.innerHTML = 'All Completed';
                    }
                })
                .catch(err => {
                    console.error('Error fetching jobs:', err);
                    jobListContainer.innerHTML = '<div class="empty-state" style="color: var(--error)">Failed to load tasks.</div>';
                });
        }

        function renderJobs(jobs) {
            if (jobs.length === 0) {
                jobListContainer.innerHTML = '<div class="empty-state"><p>No active tasks found.</p></div>';
                return;
            }

            let html = '';
            jobs.forEach(job => {
                let status = job.status;
                let progress = job.progress;

                let badgeClass = 'waiting';
                let icon = '⏳';

                if (status === 'PROCESSING' || status === 'IN_PROGRESS') {
                    badgeClass = 'processing';
                    icon = '⚙️';
                } else if (status === 'COMPLETED' || status === 'DONE') {
                    badgeClass = 'completed';
                    icon = '✅';
                } else if (status === 'ERROR') {
                    badgeClass = 'error';
                    icon = '⚠️';
                }

                html += `
        <div class="job-item">
            <div class="job-icon">\${icon}</div>
            <div class="job-info">
                <div class="job-name">\${job.name}</div>
                <span class="job-type">\${job.type}</span>
            </div>
            
            <div class="progress-wrapper">
                <div class="progress-track">
                    <div class="progress-fill" style="width: \${progress}%;"></div>
                </div>
                <div class="progress-text">\${progress}%</div>
            </div>

            <div class="job-status">
                <span class="badge \${badgeClass}">\${status}</span>
            </div>
        </div>
        `;
            });

            jobListContainer.innerHTML = html;
        }

        // Initial load
        fetchJobs();
    </script>

</body>

</html>