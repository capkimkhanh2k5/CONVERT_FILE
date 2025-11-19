<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Convert Files with One Click</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        html, body {
            height: 100%;
        }

        body {
            display: flex;
            flex-direction: column;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: linear-gradient(135deg, #e8eaf6 0%, #c5e1f5 50%, #f5f5dc 100%);
            min-height: 100vh;
            position: relative;
            overflow-x: hidden;
        }

        /* Decorative shapes */
        .shape {
            position: absolute;
            border-radius: 50%;
            opacity: 0.6;
            z-index: 0;
        }

        .shape-1 { width: 80px; height: 80px; background: white; top: 20px; right: 10%; }
        .shape-2 { width: 60px; height: 60px; background: #fff9c4; top: 20px; right: 5%; }
        .shape-3 { width: 40px; height: 40px; background: #7c4dff; top: 300px; right: 10%; }
        .shape-4 { width: 50px; height: 50px; background: #64ffda; top: 400px; left: 15%; }
        .shape-5 { width: 35px; height: 35px; background: #ff6b9d; top: 200px; left: 35%; }
        .shape-6 { width: 100px; height: 100px; background: #7c4dff; opacity: 0.7; left: 5%; bottom: 150px;}
        .shape-7 { width: 60px; height: 60px; background: #ff6b9d; opacity: 0.9; left: 35%; bottom: 350px;}
        .shape-8 { width: 50px; height: 50px; background: #64ffda; top: 500px; right: 20%; }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
            position: relative;
            z-index: 1;
        }

        /* Header */
        header {
            background: white;
            padding: 20px 0;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        }

        nav {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .logo {
            font-size: 28px;
            font-weight: bold;
        }

        .logo span:first-child {
            color: #5e35b1;
        }

        .logo span:last-child {
            color: #1e88e5;
        }

        .nav-links {
            display: flex;
            gap: 30px;
            align-items: center;
            list-style: none;
        }

        .nav-links a {
            text-decoration: none;
            color: #666;
            font-weight: 500;
            transition: color 0.3s;
        }

        .nav-links a:hover {
            color: #5e35b1;
        }

        .nav-buttons {
            display: flex;
            gap: 15px;
        }

        .nav-user {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .user-chip {
            background: #f4f4f6;
            border: 1px solid #e0e0e0;
            padding: 10px 18px;
            border-radius: 20px;
            font-weight: 600;
            color: #333;
            text-decoration: none;
        }

        .btn-signup {
            background: transparent;
            border: 1px solid #333;
            padding: 10px 25px;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 500;
            text-decoration: none;
            transition: all 0.5s ease;
        }

        .btn-login {
            background: #5e35b1;
            color: white;
            border: none;
            padding: 10px 30px;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 500;
            text-decoration: none;
            transition: all 0.5s ease;
        }

        .btn-login:hover {
            color: white;
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            background: linear-gradient(90deg, #5e35b1, #00bcd4);
        }


        /* Hero Section */
        .hero {
            padding: 80px 0;
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 60px;
            align-items: center;
        }

        .hero-content h1 {
            font-size: 56px;
            line-height: 1.2;
            margin-bottom: 20px;
            color: #1a1a1a;
        }

        .highlight-text {
            color: #5e35b1;
            position: relative;
            background: linear-gradient(90deg, #5e35b1, #00bcd4);

            background-clip: text;
            -webkit-background-clip: text;

            color: transparent;
            -webkit-text-fill-color: transparent;
        }

        .hero-content p {
            font-size: 18px;
            color: #666;
            margin-bottom: 40px;
        }

        .btn-try {
            text-decoration: none;
            background: #1a1a1a;
            color: white;
            border: none;
            padding: 18px 40px;
            font-size: 16px;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 600;
            transition: all 1s ease;
        }

        .btn-try:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            background: linear-gradient(90deg, #5e35b1, #00bcd4);
        }

        .hero-image {
            position: relative;
        }

        .laptop {
            width: 100%;
            max-width: 500px;
            position: relative;
            z-index: 2;
        }

        .circle-bg {
            position: absolute;
            width: 400px;
            height: 400px;
            background: #f5deb3;
            border-radius: 50%;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            z-index: 1;
        }

        .upload-option {
            position: absolute;
            z-index: 1;
        }

        .upload-option {
            background: white;
            padding: 12px 20px;
            border-radius: 50px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            display: flex;
            align-items: center;
            gap: 10px;
            margin: 15px 0;
            font-size: 14px;
            position: absolute;
        }

        .icon {
            width: 35px;
            height: 35px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            color: white;
            font-size: 18px;
        }

        .option-1 { 
            top: 10%; 
            left: -25%; 
        }
        .option-1 .icon { background: #7c4dff; }
        
        .option-2 { 
            top: 45%; 
            right: -35%; 
        }
        .option-2 .icon { background: #4285f4; }
        
        .option-3 { 
            bottom: 5%; 
            left: -35%;
            bottom: -10%;
        }
        .option-3 .icon { background: #1e88e5; }

        /* Features Section */
        .features {
            margin-top: auto;
        }

        .features-row {
            display: grid;
            grid-template-columns: 2fr 3fr;
            min-height: 150px;
        }

        .features-left {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 40px 30px;
        }

        .features-header h2 {
            font-size: 32px;
            color: #1a1a1a;
            line-height: 1.3;
        }

        .features-header .highlight {
            color: #1a1a1a;
            font-weight: 700;
            position: relative;
            display: inline-block;
        }

        .features-header .highlight::after {
            content: '';
            position: absolute;
            bottom: 5px;
            left: 0;
            right: 0;
            height: 15px;
            background: url("data:image/svg+xml,%3Csvg width='200' height='15' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M0,8 Q50,4 100,8 T200,8' stroke='%23ffd700' stroke-width='4' fill='none' stroke-linecap='round'/%3E%3C/svg%3E") repeat-x;
            background-size: 200px 15px;
            opacity: 0.8;
            z-index: -1;
        }

        .features-right {
            background: #1a1a1a;
            display: flex;
            align-items: flex-end;
            padding: 40px 30px 5px;
            position: relative;
            overflow: hidden;
            border-top-left-radius: 25px;
        }

        .features-right::before {
            content: '';
            position: absolute;
            width: 400px;
            height: 400px;
            background: radial-gradient(circle, rgba(124, 77, 255, 0.2) 0%, transparent 70%);
            border-radius: 50%;
            top: -50px;
            right: -100px;
            pointer-events: none;
        }

        .features-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 50px;
            position: relative;
            z-index: 1;
            width: 100%;
        }

        main {
            flex: 1 0 auto;
            display: flex;
            flex-direction: column;
        }

        .feature-card {
            padding: 0;
        }

        .feature-card h3 {
            color: white;
            font-size: 22px;
            margin-bottom: 15px;
            font-weight: 600;
        }

        .feature-card p {
            color: #999;
            line-height: 1.7;
            font-size: 14px;
        }

        @media (max-width: 768px) {
            .hero {
                grid-template-columns: 1fr;
                text-align: center;
            }

            .hero-content h1 {
                font-size: 36px;
            }

            .features-grid {
                grid-template-columns: 1fr;
            }

            .nav-links {
                display: none;
            }
        }
    </style>
</head>
<body>
    <div class="shape shape-1"></div>
    <div class="shape shape-2"></div>
    <div class="shape shape-3"></div>
    <div class="shape shape-4"></div>
    <div class="shape shape-5"></div>
    <div class="shape shape-6"></div>
    <div class="shape shape-7"></div>
    <div class="shape shape-8"></div>

    <header>
        <div class="container">
            <nav>
                <div class="logo">
                    <span>File</span><span>Convert</span>
                </div>
                <ul class="nav-links">
                    <li><a href="#converters">Converters</a></li>
                    <li><a href="#tools">Tools</a></li>
                    <li><a href="#pricing">Pricing</a></li>
                    <li><a href="#api">API</a></li>
                    <li><a href="#contact">Contact</a></li>
                </ul>
                <div class="nav-buttons">
                    <c:choose>
                        <c:when test="${not empty sessionScope.username}">
                            <div class="nav-user">
                                <span class="user-chip">${sessionScope.username}</span>
                                <a href="<c:url value='/home.jsp'/>" class="btn-login">Home</a>
                                <a href="<c:url value='/logout'/>" class="btn-login">Log Out</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <a href="<c:url value='/register'/>" class="btn-signup">Sign Up</a>
                            <a href="<c:url value='/login'/>" class="btn-login">Log In</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </nav>
        </div>
    </header>

    <main>
        <div class="container">
            <section class="hero">
                <div class="hero-content">
                    <h1>
                        Convert your all types of files with 
                        <span class="highlight-text">One Click</span> 
                    </h1>
                    <p>Simply upload your files and click the convert button.</p>
                    <a href="<c:url value='/home'/>" class="btn-try">Try it for Free</a>
                </div>
                <div class="hero-image">
                    <div class="circle-bg"></div>
                    <svg class="laptop" viewBox="0 0 500 350" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <!-- Laptop base -->
                        <rect x="50" y="280" width="400" height="10" rx="5" fill="#333"/>
                        <!-- Laptop screen frame -->
                        <rect x="100" y="80" width="300" height="200" rx="10" fill="#1a1a1a"/>
                        <!-- Screen -->
                        <rect x="110" y="90" width="280" height="180" fill="#f5f5f5"/>
                        <!-- Screen content -->
                        <rect x="130" y="110" width="240" height="30" rx="5" fill="#e0e0e0"/>
                        <rect x="140" y="155" width="80" height="15" rx="3" fill="#b39ddb"/>
                        <rect x="140" y="180" width="220" height="60" rx="5" fill="#ce93d8"/>
                        <!-- Keyboard hint -->
                        <ellipse cx="250" cy="285" rx="30" ry="3" fill="#666"/>
                    </svg>
                    
                    <div class="upload-option option-1">
                        <div class="icon">📁</div>
                        <div>
                            <div style="font-size: 11px; color: #999;">File from</div>
                            <div style="font-weight: 600;">Your Device</div>
                        </div>
                    </div>
                    
                    <div class="upload-option option-2">
                        <div class="icon">📊</div>
                        <div>
                            <div style="font-size: 11px; color: #999;">File from</div>
                            <div style="font-weight: 600;">Your Google Drive</div>
                        </div>
                    </div>
                    
                    <div class="upload-option option-3">
                        <div class="icon">📦</div>
                        <div>
                            <div style="font-size: 11px; color: #999;">File From</div>
                            <div style="font-weight: 600;">Your Dropbox</div>
                        </div>
                    </div>
                </div>
            </section>
        </div>

        <section class="features">
            <div class="features-row">
                
                <div class="features-left">
                    <div class="features-header">
                        <h2>We come with some<br><span class="highlight-text">Amazing features</span></h2>
                    </div>
                </div>
                
                <div class="features-right">
                    <div class="features-grid">
                        <div class="feature-card">

                            <h3>Easy to Use</h3>
                            <p>Simply upload your VSDX files and click the convert button. You can also batch convert VSDX to PDF format.</p>
                        </div>
                        <div class="feature-card">
                            <h3>Best Quality</h3>
                            <p>We use both open source and custom software to make sure our conversions are of the highest quality. In most cases, you can fine-tune conversion parameters using "Advanced Settings".</p>
                        </div>
                        <div class="feature-card">
                            <h3>Free & Secure</h3>
                            <p>Our VSDX to PDF Converter is free and works on any web browser. We guarantee file security and privacy. Files are protected with 256-bit SSL encryption and automatically deleted after 2 hours.</p>
                        </div>
                    </div>
                </div>

            </div>
        </section>
    </main>

    <script>
        // Add smooth scrolling
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                    target.scrollIntoView({ behavior: 'smooth' });
                }
            });
        });

        // Add hover effects
        document.querySelectorAll('.btn-try, .btn-login, .btn-signup').forEach(button => {
            button.addEventListener('mouseenter', function() {
                this.style.transform = 'translateY(-2px)';
            });
            button.addEventListener('mouseleave', function() {
                this.style.transform = 'translateY(0)';
            });
        });
    </script>
</body>
</html>
