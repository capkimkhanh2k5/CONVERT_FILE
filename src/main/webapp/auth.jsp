<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Welcome to ConvertFile</title>

    <!-- Google Sign-In Library -->
    <script src="https://accounts.google.com/gsi/client" async defer></script>

    <!-- Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700;800&display=swap"
        rel="stylesheet">

    <style>
        :root {
            --primary: #6366f1;
            --primary-dark: #4f46e5;
            --secondary: #ec4899;
            --accent: #8b5cf6;
            --text-main: #1e293b;
            --text-light: #64748b;
            --bg-glass: rgba(255, 255, 255, 0.85);
            --shadow-glass: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
            --border-glass: 1px solid rgba(255, 255, 255, 0.18);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Outfit', sans-serif;
        }

        body {
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            background: #f0f2f5;
            overflow-x: hidden;
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
            opacity: 0.6;
            animation: float 20s infinite ease-in-out;
        }

        .orb-1 {
            width: 400px;
            height: 400px;
            background: var(--primary);
            top: -100px;
            left: -100px;
            animation-delay: 0s;
        }

        .orb-2 {
            width: 300px;
            height: 300px;
            background: var(--secondary);
            bottom: -50px;
            right: -50px;
            animation-delay: -5s;
        }

        .orb-3 {
            width: 350px;
            height: 350px;
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

        /* Back Button */
        .back-btn {
            position: fixed;
            top: 24px;
            left: 24px;
            padding: 12px 24px;
            background: white;
            border-radius: 30px;
            text-decoration: none;
            color: var(--text-main);
            font-weight: 600;
            font-size: 14px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
            transition: all 0.3s ease;
            z-index: 100;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .back-btn:hover {
            transform: translateX(-4px);
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
        }

        /* Main Container */
        .auth-container {
            width: 1000px;
            max-width: 95vw;
            min-height: 600px;
            background: var(--bg-glass);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border-radius: 24px;
            border: var(--border-glass);
            box-shadow: var(--shadow-glass);
            display: flex;
            overflow: hidden;
            position: relative;
            transition: all 0.5s ease;
        }

        /* Left Side - Image/Brand */
        .brand-side {
            flex: 1;
            background: linear-gradient(135deg, rgba(99, 102, 241, 0.1), rgba(236, 72, 153, 0.1));
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            padding: 40px;
            position: relative;
            overflow: hidden;
            transition: transform 0.6s ease-in-out;
        }

        .brand-content {
            text-align: center;
            z-index: 2;
            animation: fadeIn Up 0.8s ease;
        }

        .brand-logo {
            font-size: 48px;
            font-weight: 800;
            background: linear-gradient(135deg, var(--primary), var(--secondary));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin-bottom: 16px;
            display: inline-block;
        }

        .brand-text {
            color: var(--text-light);
            font-size: 16px;
            line-height: 1.6;
            max-width: 300px;
            margin: 0 auto;
        }

        .brand-img {
            width: 80%;
            max-width: 300px;
            margin-top: 40px;
            filter: drop-shadow(0 10px 20px rgba(0, 0, 0, 0.1));
            transition: transform 0.5s ease;
        }

        .brand-side:hover .brand-img {
            transform: scale(1.05) rotate(-2deg);
        }

        /* Right Side - Forms */
        .form-side {
            flex: 1;
            position: relative;
            background: white;
            transition: transform 0.6s ease-in-out;
        }

        .form-wrapper {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            padding: 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            transition: all 0.6s ease-in-out;
            opacity: 0;
            visibility: hidden;
            transform: translateX(20px);
        }

        .form-wrapper.active {
            opacity: 1;
            visibility: visible;
            transform: translateX(0);
        }

        .form-title {
            font-size: 32px;
            font-weight: 700;
            color: var(--text-main);
            margin-bottom: 8px;
        }

        .form-subtitle {
            color: var(--text-light);
            font-size: 14px;
            margin-bottom: 32px;
        }

        /* Inputs */
        .input-group {
            margin-bottom: 20px;
            position: relative;
        }

        .input-field {
            width: 100%;
            padding: 16px;
            background: #f8fafc;
            border: 2px solid transparent;
            border-radius: 12px;
            font-size: 15px;
            color: var(--text-main);
            transition: all 0.3s ease;
        }

        .input-field:focus {
            outline: none;
            background: white;
            border-color: var(--primary);
            box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
        }

        .input-label {
            position: absolute;
            left: 16px;
            top: 16px;
            color: #94a3b8;
            font-size: 15px;
            pointer-events: none;
            transition: all 0.3s ease;
        }

        .input-field:focus~.input-label,
        .input-field:not(:placeholder-shown)~.input-label {
            transform: translateY(-28px) scale(0.85);
            left: 8px;
            color: var(--primary);
            font-weight: 600;
        }

        /* Button */
        .submit-btn {
            width: 100%;
            padding: 16px;
            background: linear-gradient(135deg, var(--primary), var(--accent));
            color: white;
            border: none;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
            margin-top: 10px;
        }

        .submit-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(99, 102, 241, 0.4);
        }

        .submit-btn:active {
            transform: translateY(0);
        }

        /* Social Login */
        .divider {
            display: flex;
            align-items: center;
            margin: 24px 0;
            color: var(--text-light);
            font-size: 13px;
        }

        .divider::before,
        .divider::after {
            content: "";
            flex: 1;
            height: 1px;
            background: #e2e8f0;
        }

        .divider span {
            padding: 0 16px;
        }

        .social-btn {
            width: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
            margin-bottom: 20px;
        }

        /* Switch Mode */
        .switch-text {
            text-align: center;
            margin-top: 24px;
            color: var(--text-light);
            font-size: 14px;
        }

        .switch-link {
            color: var(--primary);
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            transition: color 0.3s ease;
        }

        .switch-link:hover {
            color: var(--primary-dark);
            text-decoration: underline;
        }

        /* Messages */
        .message {
            padding: 12px 16px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 14px;
            display: flex;
            align-items: center;
            gap: 8px;
            animation: slideDown 0.3s ease;
        }

        .message.error {
            background: #fef2f2;
            color: #ef4444;
            border: 1px solid #fee2e2;
        }

        .message.success {
            background: #f0fdf4;
            color: #22c55e;
            border: 1px solid #dcfce7;
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

        /* Password Toggle */
        .password-toggle {
            position: absolute;
            right: 16px;
            top: 16px;
            cursor: pointer;
            color: #94a3b8;
            transition: color 0.3s ease;
        }

        .password-toggle:hover {
            color: var(--primary);
        }

        /* Responsive */
        @media (max-width: 768px) {
            .auth-container {
                flex-direction: column;
                height: auto;
                min-height: auto;
            }

            .brand-side {
                padding: 30px;
                min-height: 200px;
            }

            .brand-img {
                display: none;
            }

            .form-wrapper {
                position: relative;
                height: auto;
                padding: 30px;
                opacity: 1;
                visibility: visible;
                transform: none;
                display: none;
            }

            .form-wrapper.active {
                display: flex;
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

    <a href="<c:url value='/home'/>" class="back-btn">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
            stroke-linecap="round" stroke-linejoin="round">
            <path d="M19 12H5M12 19l-7-7 7-7" />
        </svg>
        Back to Home
    </a>

    <% String activeForm=request.getParameter("form"); if (activeForm==null) { Object
        formAttr=request.getAttribute("activeForm"); if (formAttr !=null) { activeForm=formAttr.toString(); } }
        if (activeForm==null) { activeForm="login" ; } %>

        <div class="auth-container" id="authContainer" data-initial-form="<%= activeForm %>">
            <!-- Brand Side -->
            <div class="brand-side">
                <div class="brand-content">
                    <div class="brand-logo">ConvertFile</div>
                    <p class="brand-text">Transform your documents with ease. Fast, secure, and reliable file
                        conversion for everyone.</p>
                </div>
                <img src="<c:url value='/resources/img/IMAGE_LOGIN.png'/>" alt="Illustration" class="brand-img">
            </div>

            <!-- Form Side -->
            <div class="form-side">

                <!-- LOGIN FORM -->
                <div class="form-wrapper" id="loginFormWrapper">
                    <h1 class="form-title">Welcome Back</h1>
                    <p class="form-subtitle">Please enter your details to sign in.</p>

                    <% if ("login".equals(activeForm) && request.getAttribute("errorMessage") !=null) { %>
                        <div class="message error">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <line x1="12" y1="8" x2="12" y2="12"></line>
                                <line x1="12" y1="16" x2="12.01" y2="16"></line>
                            </svg>
                            <%= request.getAttribute("errorMessage") %>
                        </div>
                        <% } %>
                            <% if ("login".equals(activeForm) && request.getAttribute("successMessage") !=null)
                                { %>
                                <div class="message success">
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                        stroke="currentColor" stroke-width="2">
                                        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                                        <polyline points="22 4 12 14.01 9 11.01"></polyline>
                                    </svg>
                                    <%= request.getAttribute("successMessage") %>
                                </div>
                                <% } %>

                                    <form action="login" method="post" id="loginForm">
                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                                        <div class="input-group">
                                            <input type="text" name="username" id="loginUsername"
                                                class="input-field" placeholder=" " required>
                                            <label for="loginUsername" class="input-label">Username</label>
                                        </div>

                                        <div class="input-group">
                                            <input type="password" name="password" id="loginPassword"
                                                class="input-field" placeholder=" " required>
                                            <label for="loginPassword" class="input-label">Password</label>
                                            <span class="password-toggle"
                                                onclick="togglePassword('loginPassword')">
                                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                                    stroke="currentColor" stroke-width="2"
                                                    stroke-linecap="round" stroke-linejoin="round">
                                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z">
                                                    </path>
                                                    <circle cx="12" cy="12" r="3"></circle>
                                                </svg>
                                            </span>
                                        </div>

                                        <div
                                            style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; font-size: 14px;">
                                            <label
                                                style="display: flex; align-items: center; gap: 8px; color: var(--text-light); cursor: pointer;">
                                                <input type="checkbox" name="remember"
                                                    style="accent-color: var(--primary);"> Remember me
                                            </label>
                                            <a href="<c:url value='/forgot-password'/>"
                                                style="color: var(--primary); text-decoration: none; font-weight: 500;">Forgot
                                                Password?</a>
                                        </div>

                                        <button type="submit" class="submit-btn" name="action"
                                            value="loginBtn">Sign In</button>
                                    </form>

                                    <div class="divider"><span>or continue with</span></div>

                                    <div class="social-btn">
                                        <div id="g_id_onload" data-client_id="${googleClientId}"
                                            data-context="signin" data-ux_mode="popup"
                                            data-callback="handleCredentialResponse" data-auto_prompt="false">
                                        </div>
                                        <div class="g_id_signin" data-type="standard" data-shape="rectangular"
                                            data-theme="outline" data-text="signin_with" data-size="large"
                                            data-logo_alignment="center" data-width="100%">
                                        </div>
                                    </div>

                                    <div class="switch-text">
                                        Don't have an account? <span class="switch-link"
                                            onclick="switchMode('register')">Sign up now</span>
                                    </div>
                </div>

                <!-- REGISTER FORM -->
                <div class="form-wrapper" id="registerFormWrapper">
                    <h1 class="form-title">Create Account</h1>
                    <p class="form-subtitle">Join us and start converting files today.</p>

                    <% if ("register".equals(activeForm) && request.getAttribute("errorMessage") !=null) { %>
                        <div class="message error">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <line x1="12" y1="8" x2="12" y2="12"></line>
                                <line x1="12" y1="16" x2="12.01" y2="16"></line>
                            </svg>
                            <%= request.getAttribute("errorMessage") %>
                        </div>
                        <% } %>
                            <% if ("register".equals(activeForm) && request.getAttribute("successMessage")
                                !=null) { %>
                                <div class="message success">
                                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                        stroke="currentColor" stroke-width="2">
                                        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                                        <polyline points="22 4 12 14.01 9 11.01"></polyline>
                                    </svg>
                                    <%= request.getAttribute("successMessage") %>
                                </div>
                                <% } %>

                                    <form action="register" method="post" id="registerForm">
                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                                        <div class="input-group">
                                            <input type="text" name="username" id="regUsername"
                                                class="input-field" placeholder=" " required minlength="3">
                                            <label for="regUsername" class="input-label">Username</label>
                                        </div>

                                        <div class="input-group">
                                            <input type="email" name="email" id="regEmail" class="input-field"
                                                placeholder=" " required>
                                            <label for="regEmail" class="input-label">Email Address</label>
                                        </div>

                                        <div class="input-group">
                                            <input type="password" name="password" id="regPassword"
                                                class="input-field" placeholder=" " required minlength="6">
                                            <label for="regPassword" class="input-label">Password</label>
                                            <span class="password-toggle"
                                                onclick="togglePassword('regPassword')">
                                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                                    stroke="currentColor" stroke-width="2"
                                                    stroke-linecap="round" stroke-linejoin="round">
                                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z">
                                                    </path>
                                                    <circle cx="12" cy="12" r="3"></circle>
                                                </svg>
                                            </span>
                                        </div>

                                        <div class="input-group">
                                            <input type="password" name="confirmPassword" id="regConfirm"
                                                class="input-field" placeholder=" " required minlength="6">
                                            <label for="regConfirm" class="input-label">Confirm Password</label>
                                        </div>

                                        <button type="submit" class="submit-btn">Create Account</button>
                                    </form>

                                    <div class="divider"><span>or sign up with</span></div>

                                    <div class="social-btn">
                                        <!-- Google Button reused logic -->
                                        <div class="g_id_signin" data-type="standard" data-shape="rectangular"
                                            data-theme="outline" data-text="signup_with" data-size="large"
                                            data-logo_alignment="center" data-width="100%">
                                        </div>
                                    </div>

                                    <div class="switch-text">
                                        Already have an account? <span class="switch-link"
                                            onclick="switchMode('login')">Sign in</span>
                                    </div>
                </div>

            </div>
        </div>

        <script>
            // State Management
            const loginWrapper = document.getElementById('loginFormWrapper');
            const registerWrapper = document.getElementById('registerFormWrapper');
            const authContainer = document.getElementById('authContainer');

            function switchMode(mode) {
                if (mode === 'register') {
                    loginWrapper.classList.remove('active');
                    setTimeout(() => {
                        registerWrapper.classList.add('active');
                    }, 200);
                    // Optional: Change brand side content or style here
                } else {
                    registerWrapper.classList.remove('active');
                    setTimeout(() => {
                        loginWrapper.classList.add('active');
                    }, 200);
                }
            }

            // Initial Load
            window.addEventListener('DOMContentLoaded', () => {
                const initialForm = authContainer.dataset.initialForm || 'login';
                if (initialForm === 'register') {
                    registerWrapper.classList.add('active');
                } else {
                    loginWrapper.classList.add('active');
                }
            });

            // Password Toggle
            function togglePassword(inputId) {
                const input = document.getElementById(inputId);
                input.type = input.type === 'password' ? 'text' : 'password';
            }

            // Form Validation
            document.getElementById('registerForm').addEventListener('submit', function (e) {
                const pass = document.getElementById('regPassword').value;
                const confirm = document.getElementById('regConfirm').value;

                if (pass !== confirm) {
                    e.preventDefault();
                    alert('Passwords do not match!');
                }
            });

            // Google Sign-In Handler
            function handleCredentialResponse(response) {
                console.log("Encoded JWT ID token: " + response.credential);

                // Clear cookie
                document.cookie = "g_state=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";

                fetch('google-login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: 'credential=' + encodeURIComponent(response.credential)
                })
                    .then(res => res.json())
                    .then(data => {
                        if (data.success) {
                            window.location.href = '<c:url value="/home"/>';
                        } else {
                            alert(data.error || 'Login failed');
                        }
                    })
                    .catch(err => {
                        console.error(err);
                        alert('Network error');
                    });
            }
        </script>
</body>

</html>