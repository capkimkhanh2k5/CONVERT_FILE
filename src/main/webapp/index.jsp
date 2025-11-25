<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ConvertFile - Modern File Conversion</title>

    <script>
        // Xóa cookie g_state bị lỗi nếu có
        document.cookie = "g_state=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    </script>

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
            --bg-glass: rgba(255, 255, 255, 0.7);
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
            overflow-x: hidden;
            color: var(--text-main);
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
            width: 600px;
            height: 600px;
            background: var(--primary);
            top: -20%;
            left: -10%;
            animation-delay: 0s;
        }

        .orb-2 {
            width: 500px;
            height: 500px;
            background: var(--secondary);
            bottom: -10%;
            right: -10%;
            animation-delay: -5s;
        }

        .orb-3 {
            width: 400px;
            height: 400px;
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

        /* Layout */
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 24px;
        }

        /* Header */
        header {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            z-index: 100;
            padding: 20px 0;
            transition: all 0.3s ease;
        }

        header.scrolled {
            background: rgba(255, 255, 255, 0.8);
            backdrop-filter: blur(20px);
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
            padding: 16px 0;
        }

        nav {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .logo {
            font-size: 28px;
            font-weight: 800;
            background: linear-gradient(135deg, var(--primary), var(--secondary));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            text-decoration: none;
        }

        .nav-links {
            display: flex;
            gap: 32px;
            list-style: none;
        }

        .nav-links a {
            text-decoration: none;
            color: var(--text-main);
            font-weight: 500;
            font-size: 15px;
            transition: color 0.3s ease;
        }

        .nav-links a:hover {
            color: var(--primary);
        }

        .nav-actions {
            display: flex;
            gap: 16px;
            align-items: center;
        }

        .btn {
            padding: 10px 24px;
            border-radius: 12px;
            font-weight: 600;
            font-size: 14px;
            text-decoration: none;
            transition: all 0.3s ease;
            cursor: pointer;
            border: none;
        }

        .btn-ghost {
            color: var(--text-main);
            background: rgba(255, 255, 255, 0.5);
            border: 1px solid transparent;
        }

        .btn-ghost:hover {
            background: white;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        }

        .btn-primary {
            background: linear-gradient(135deg, var(--primary), var(--accent));
            color: white;
            box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(99, 102, 241, 0.4);
        }

        .user-chip {
            padding: 8px 16px;
            background: white;
            border-radius: 30px;
            font-weight: 600;
            color: var(--text-main);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        }

        /* Hero Section */
        .hero {
            padding: 180px 0 100px;
            margin-left: 40px;
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 60px;
            align-items: center;
        }

        .hero-content h1 {
            font-size: 64px;
            line-height: 1.1;
            margin-bottom: 24px;
            color: var(--text-main);
            letter-spacing: -1px;
        }

        .gradient-text {
            background: linear-gradient(135deg, var(--primary), var(--secondary));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .hero-content p {
            font-size: 18px;
            color: var(--text-light);
            margin-bottom: 40px;
            line-height: 1.6;
            max-width: 500px;
        }

        .hero-btns {
            display: flex;
            gap: 16px;
        }

        .btn-lg {
            padding: 16px 32px;
            font-size: 16px;
            border-radius: 16px;
        }

        /* Hero Visual */
        .hero-visual {
            position: relative;
            height: 500px;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .glass-card {
            background: var(--bg-glass);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border: var(--border-glass);
            border-radius: 24px;
            box-shadow: var(--shadow-glass);
            position: absolute;
            display: flex;
            align-items: center;
            gap: 16px;
            padding: 20px;
            animation: floatCard 6s infinite ease-in-out;
        }

        .card-main {
            width: 380px;
            height: 240px;
            z-index: 2;
            flex-direction: column;
            justify-content: center;
            text-align: center;
        }

        .card-icon {
            width: 64px;
            height: 64px;
            background: white;
            border-radius: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 32px;
            margin-bottom: 16px;
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.05);
        }

        .card-1 {
            top: 10%;
            right: 10%;
            animation-delay: -2s;
            padding: 12px 20px;
            border-radius: 16px;
        }

        .card-2 {
            bottom: 15%;
            left: 5%;
            animation-delay: -4s;
            padding: 12px 20px;
            border-radius: 16px;
        }

        @keyframes floatCard {

            0%,
            100% {
                transform: translateY(0);
            }

            50% {
                transform: translateY(-20px);
            }
        }

        /* Features Section */
        .features {
            padding: 100px 0;
        }

        .section-header {
            text-align: center;
            margin-bottom: 60px;
        }

        .section-header h2 {
            font-size: 42px;
            margin-bottom: 16px;
        }

        .features-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 32px;
        }

        .feature-card {
            background: rgba(255, 255, 255, 0.6);
            backdrop-filter: blur(20px);
            padding: 40px;
            border-radius: 24px;
            border: 1px solid rgba(255, 255, 255, 0.5);
            transition: all 0.3s ease;
        }

        .feature-card:hover {
            transform: translateY(-10px);
            background: white;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.05);
        }

        .feature-icon {
            width: 56px;
            height: 56px;
            background: linear-gradient(135deg, #f0f9ff, #e0f2fe);
            color: var(--primary);
            border-radius: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            margin-bottom: 24px;
        }

        .feature-card:nth-child(2) .feature-icon {
            background: linear-gradient(135deg, #fdf2f8, #fce7f3);
            color: var(--secondary);
        }

        .feature-card:nth-child(3) .feature-icon {
            background: linear-gradient(135deg, #f5f3ff, #ede9fe);
            color: var(--accent);
        }

        .feature-card h3 {
            font-size: 20px;
            margin-bottom: 12px;
            color: var(--text-main);
        }

        .feature-card p {
            color: var(--text-light);
            line-height: 1.6;
            font-size: 15px;
        }

        /* Responsive */
        @media (max-width: 1024px) {
            .hero {
                grid-template-columns: 1fr;
                text-align: center;
                padding-top: 140px;
            }

            .hero-content p {
                margin: 0 auto 40px;
            }

            .hero-btns {
                justify-content: center;
            }

            .hero-visual {
                display: none;
            }

            /* Hide complex visual on tablet/mobile for simplicity */

            .features-grid {
                grid-template-columns: 1fr;
            }

            .nav-links {
                display: none;
            }

            /* Simple hide for now */
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

    <!-- Header -->
    <header id="header">
        <div class="container">
            <nav>
                <a href="#" class="logo">ConvertFile</a>

                <ul class="nav-links">
                    <li><a href="#features">Features</a></li>
                    <li><a href="#">Pricing</a></li>
                    <li><a href="#">API</a></li>
                    <li><a href="#">Support</a></li>
                </ul>

                <div class="nav-actions">
                    <c:choose>
                        <c:when test="${not empty sessionScope.username}">
                            <span class="user-chip">Hi, ${sessionScope.username}</span>
                            <a href="<c:url value='/home'/>" class="btn btn-primary">Dashboard</a>
                            <a href="<c:url value='/logout'/>" class="btn btn-ghost">Logout</a>
                        </c:when>
                        <c:otherwise>
                            <a href="<c:url value='/login'/>" class="btn btn-ghost">Log In</a>
                            <a href="<c:url value='/register'/>" class="btn btn-primary">Sign Up Free</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </nav>
        </div>
    </header>

    <!-- Hero Section -->
    <section class="hero">
        <div class="container" style="display: contents;">
            <div class="hero-content">
                <h1>Convert any file with <br><span class="gradient-text">One Click.</span></h1>
                <p>The most advanced file converter on the web. Fast, secure, and free for everyone. Supports
                    PDF, Images, Word, and more.</p>
                <div class="hero-btns">
                    <a href="<c:url value='/home'/>" class="btn btn-primary btn-lg">Start Converting Now</a>
                    <a href="#features" class="btn btn-ghost btn-lg">Learn More</a>
                </div>
            </div>

            <div class="hero-visual">
                <!-- Main Floating Card -->
                <div class="glass-card card-main">
                    <div class="card-icon">🚀</div>
                    <h3>Super Fast Conversion</h3>
                    <p style="color: var(--text-light); font-size: 14px; margin-top: 8px;">Drag & drop your
                        files and watch the magic happen instantly.</p>
                </div>

                <!-- Floating Elements -->
                <div class="glass-card card-1">
                    <span style="font-size: 20px;">📄</span>
                    <span style="font-weight: 600; font-size: 14px;">PDF to Word</span>
                </div>

                <div class="glass-card card-2">
                    <span style="font-size: 20px;">🖼️</span>
                    <span style="font-weight: 600; font-size: 14px;">Image to PDF</span>
                </div>
            </div>
        </div>
    </section>

    <!-- Features Section -->
    <section id="features" class="features">
        <div class="container">
            <div class="section-header">
                <h2>Why choose <span class="gradient-text">ConvertFile?</span></h2>
                <p style="color: var(--text-light); font-size: 18px;">Everything you need to manage your
                    documents.</p>
            </div>

            <div class="features-grid">
                <div class="feature-card">
                    <div class="feature-icon">⚡</div>
                    <h3>Lightning Fast</h3>
                    <p>Our optimized servers ensure your files are converted in seconds, not minutes. No waiting
                        queues.</p>
                </div>

                <div class="feature-card">
                    <div class="feature-icon">🛡️</div>
                    <h3>Secure & Private</h3>
                    <p>Your files are encrypted during transfer and automatically deleted from our servers after
                        2 hours.</p>
                </div>

                <div class="feature-card">
                    <div class="feature-icon">✨</div>
                    <h3>High Quality</h3>
                    <p>We use advanced algorithms to ensure the best possible quality for your converted
                        documents and images.</p>
                </div>
            </div>
        </div>
    </section>

    <script>
        // Header Scroll Effect
        window.addEventListener('scroll', () => {
            const header = document.getElementById('header');
            if (window.scrollY > 50) {
                header.classList.add('scrolled');
            } else {
                header.classList.remove('scrolled');
            }
        });

        // Smooth Scroll
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                    target.scrollIntoView({ behavior: 'smooth' });
                }
            });
        });
    </script>
</body>

</html>