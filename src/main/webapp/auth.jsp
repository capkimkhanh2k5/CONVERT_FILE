<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login / Register</title>

    <!-- 
        Cập nhật các link trong code:
        href="login.jsp" → href="auth.jsp"
        href="register.jsp" → href="auth.jsp?form=register"
    -->



    <!-- Google Sign-In Library -->
    <script src="https://accounts.google.com/gsi/client" async defer></script>

    <style>
        html {
            overflow-x: hidden;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #f5f7fa 0%, #e8ebf0 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 2vw;
            position: relative;
            overflow-x: hidden;
        }

        /* Back Arrow - Outside container */
        .back-arrow {
            position: fixed;
            top: 20px;
            right: 20px;
            color: #1a1a1a;
            font-size: clamp(14px, 1.5vw, 16px);
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 8px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.3s ease;
            background: white;
            padding: 10px 18px;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            z-index: 1000;
        }

        .back-arrow:hover {
            transform: translateX(-5px);
            box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
            background: #f8f9fa;
        }

        .auth-container {
            display: flex;
            background: white;
            border-radius: clamp(16px, 2vw, 24px);
            overflow: hidden;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
            width: 95vw;
            max-width: min(1400px, 95vw);
            height: auto;
            max-height: 90vh;
            margin-top: 20px;
            position: relative;
        }

        /* Image Side */
        .image-side {
            flex: 0 0 55%;
            position: relative;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
            min-height: 600px;
            transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
            order: 1;
        }

        .auth-container.register-mode .image-side {
            order: 2;
        }

        .image-side img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            object-position: center;
            display: block;
        }

        /* Form Side Container */
        .form-side {
            flex: 1;
            position: relative;
            overflow: hidden;
            transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
            order: 2;
        }

        .auth-container.register-mode .form-side {
            order: 1;
        }

        /* Forms Wrapper */
        .forms-wrapper {
            position: relative;
            width: 100%;
            height: 100%;
        }

        /* Individual Forms */
        .login-form,
        .register-form {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            padding: clamp(40px, 5vh, 60px) clamp(30px, 4vw, 50px);
            display: flex;
            flex-direction: column;
            justify-content: center;
            overflow-y: auto;
            background: white;
            transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
            opacity: 1;
            visibility: visible;
        }

        /* Login Form States */
        .login-form {
            transform: translateX(0);
        }

        .auth-container.register-mode .login-form {
            transform: translateX(-100%);
            opacity: 0;
            visibility: hidden;
        }

        /* Register Form States */
        .register-form {
            transform: translateX(100%);
            opacity: 0;
            visibility: hidden;
        }

        .auth-container.register-mode .register-form {
            transform: translateX(0);
            opacity: 1;
            visibility: visible;
        }

        .form-header {
            margin-bottom: 30px;
        }

        .form-header h1 {
            font-size: clamp(26px, 2.5vw, 34px);
            color: #1a1a1a;
            font-weight: 800;
            margin-bottom: 8px;
            line-height: 1.2;
        }

        .form-header p {
            color: #666;
            font-size: clamp(13px, 1.2vw, 15px);
            margin-bottom: 0;
        }

        .form-group {
            margin-bottom: 18px;
        }

        .form-group label {
            display: block;
            color: #1a1a1a;
            font-weight: 600;
            margin-bottom: 8px;
            font-size: 14px;
        }

        .input-wrapper {
            position: relative;
        }

        input[type="text"],
        input[type="password"],
        input[type="email"] {
            width: 100%;
            padding: 13px 18px;
            border: 2px solid #e8ebf0;
            border-radius: 10px;
            font-size: 14px;
            transition: all 0.3s ease;
            background: #f8f9fa;
            font-family: inherit;
        }

        input:focus {
            outline: none;
            border-color: #ff6b9d;
            background: white;
            box-shadow: 0 0 0 4px rgba(255, 107, 157, 0.1);
        }

        .password-toggle {
            position: absolute;
            right: 14px;
            top: 50%;
            transform: translateY(-50%);
            cursor: pointer;
            color: #999;
            font-size: 18px;
            transition: color 0.3s ease;
            user-select: none;
        }

        .password-toggle:hover {
            color: #ff6b9d;
        }

        .password-strength {
            margin-top: 8px;
            height: 4px;
            background: #e0e0e0;
            border-radius: 2px;
            overflow: hidden;
            display: none;
        }

        .password-strength.show {
            display: block;
        }

        .password-strength-bar {
            height: 100%;
            width: 0%;
            transition: all 0.3s ease;
            border-radius: 2px;
        }

        .strength-weak { background: #f44336; width: 33%; }
        .strength-medium { background: #ff9800; width: 66%; }
        .strength-strong { background: #4caf50; width: 100%; }

        .password-hint {
            font-size: 11px;
            color: #999;
            margin-top: 5px;
        }

        .password-match {
            font-size: 12px;
            margin-top: 5px;
            display: none;
        }

        .password-match.show {
            display: block;
        }

        .password-match.match {
            color: #4caf50;
        }

        .password-match.no-match {
            color: #f44336;
        }

        .form-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
            margin-top: 5px;
        }

        .remember-me {
            display: flex;
            align-items: center;
            gap: 8px;
            color: #666;
            font-size: 13px;
            cursor: pointer;
        }

        .remember-me input[type="checkbox"] {
            width: 17px;
            height: 17px;
            cursor: pointer;
            accent-color: #ff6b9d;
        }

        .forgot-link {
            color: #ff6b9d;
            text-decoration: none;
            font-size: 13px;
            font-weight: 600;
            transition: color 0.3s ease;
        }

        .forgot-link:hover {
            color: #ff4d88;
            text-decoration: underline;
        }

        .submit-btn {
            width: 100%;
            padding: 15px;
            background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 15px;
            font-weight: 700;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 8px 20px rgba(255, 107, 157, 0.3);
            margin-top: 10px;
        }

        .submit-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 28px rgba(255, 107, 157, 0.4);
        }

        .submit-btn:active {
            transform: translateY(0);
        }

        .submit-btn:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }

        .message {
            padding: 14px 18px;
            border-radius: 10px;
            margin-top: 15px;
            font-size: 13px;
            font-weight: 500;
            display: none;
        }

        .message.show {
            display: block;
            animation: slideIn 0.3s ease;
        }

        .message.success {
            background: #e8f5e9;
            color: #2e7d32;
            border: 2px solid #c8e6c9;
        }

        .message.error {
            background: #ffebee;
            color: #c62828;
            border: 2px solid #ffcdd2;
        }

        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .divider {
            text-align: center;
            margin: 18px 0;
            position: relative;
            color: #999;
            font-size: 12px;
        }

        .divider::before,
        .divider::after {
            content: '';
            position: absolute;
            top: 50%;
            width: 38%;
            height: 1px;
            background: #e8ebf0;
        }

        .divider::before { left: 0; }
        .divider::after { right: 0; }

        .switch-link {
            text-align: center;
            color: #666;
            font-size: 13px;
            margin-top: 15px;
            margin-bottom: 0;
        }

        .switch-link a {
            color: #ff6b9d;
            text-decoration: none;
            font-weight: 700;
            transition: color 0.3s ease;
            cursor: pointer;
        }

        .switch-link a:hover {
            color: #ff4d88;
            text-decoration: underline;
        }

        .social-btn {
            flex: 1;
            padding: 13px;
            border: 2px solid #e8ebf0;
            border-radius: 10px;
            background: white;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            font-size: 13px;
            font-weight: 600;
            color: #1a1a1a;
            transition: all 0.3s ease;
            text-decoration: none;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            z-index: 1000;
        }

        .social-btn:hover {
            border-color: #ff6b9d;
            background: #fef5f8;
            transform: translateY(-1px);
        }

        .google-icon {
            width: 18px;
            height: 18px;
        }

        .social-login {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 12px;
            margin-bottom: 20px;
        }

        /* Responsive Tablet */
        @media (max-width: 1024px) {
            .auth-container {
                max-width: 900px;
            }

            .image-side {
                flex: 0 0 50%;
                min-height: 500px;
            }

            .login-form,
            .register-form {
                padding: 35px 30px;
            }
        }

        /* Responsive Mobile */
        @media (max-width: 768px) {
            body {
                padding: 0;
                align-items: flex-start;
            }

            .back-arrow {
                top: 15px;
                right: 15px;
                padding: 8px 14px;
                font-size: 13px;
                gap: 6px;
            }

            .auth-container {
                flex-direction: column;
                max-height: none;
                height: auto;
                width: 100vw;
                max-width: 100vw;
                border-radius: 0;
                margin-top: 0;
                padding-top: 60px;
            }

            .auth-container.register-mode {
                flex-direction: column;
            }

            .image-side {
                min-height: 250px;
                flex: 0 0 auto;
                order: 1 !important;
            }

            .form-side {
                order: 2 !important;
                min-height: calc(100vh - 250px - 60px);
            }

            .forms-wrapper {
                position: static;
                height: auto;
                min-height: calc(100vh - 250px - 60px);
            }

            .login-form,
            .register-form {
                position: static;
                padding: 30px 25px 40px;
                height: auto;
                min-height: calc(100vh - 250px - 60px);
            }

            .login-form {
                display: flex;
            }

            .auth-container.register-mode .login-form {
                display: none;
                transform: none;
                opacity: 1;
                visibility: visible;
            }

            .register-form {
                display: none;
            }

            .auth-container.register-mode .register-form {
                display: flex;
                transform: none;
                opacity: 1;
                visibility: visible;
            }

            .form-header h1 {
                font-size: 24px;
            }

            .form-header p {
                font-size: 13px;
            }

            .form-footer {
                flex-direction: column;
                gap: 12px;
                align-items: flex-start;
            }
        }

        /* Extra Small Mobile */
        @media (max-width: 480px) {
            .back-arrow {
                top: 10px;
                right: 10px;
                padding: 7px 12px;
                font-size: 12px;
            }

            .auth-container {
                padding-top: 50px;
            }

            .login-form,
            .register-form {
                padding: 25px 20px 35px;
                min-height: calc(100vh - 200px - 50px);
            }

            .image-side {
                min-height: 200px;
            }

            .form-side {
                min-height: calc(100vh - 200px - 50px);
            }

            .forms-wrapper {
                min-height: calc(100vh - 200px - 50px);
            }

            .social-login {
                flex-direction: column;
            }

            .form-group {
                margin-bottom: 16px;
            }

            input[type="text"],
            input[type="password"],
            input[type="email"] {
                padding: 12px 16px;
                font-size: 14px;
            }

            .submit-btn {
                padding: 14px;
                font-size: 14px;
            }
        }

        /* Scrollbar Styling */
        .login-form::-webkit-scrollbar,
        .register-form::-webkit-scrollbar {
            width: 6px;
        }

        .login-form::-webkit-scrollbar-track,
        .register-form::-webkit-scrollbar-track {
            background: #f1f1f1;
        }

        .login-form::-webkit-scrollbar-thumb,
        .register-form::-webkit-scrollbar-thumb {
            background: #ff6b9d;
            border-radius: 3px;
        }

        .login-form::-webkit-scrollbar-thumb:hover,
        .register-form::-webkit-scrollbar-thumb:hover {
            background: #ff4d88;
        }
    </style>
</head>
<body>
    <!-- Back Arrow - Fixed Position -->
    <a href="home.jsp" class="back-arrow">← HOME</a>

    <div class="auth-container" id="authContainer">
        <!-- Image Side -->
        <div class="image-side">
            <img src="${pageContext.request.contextPath}/resources/img/IMAGE_LOGIN.png" alt="Auth Image">
        </div>

        <!-- Form Side -->
        <div class="form-side">
            <div class="forms-wrapper">
                <!-- LOGIN FORM -->
                <div class="login-form">
                    <div class="form-header">
                        <h1>📁 Welcome Back!</h1>
                        <p>Login to your account to continue</p>
                    </div>

                    <!-- Login Messages -->
                    <% if ("login".equals(request.getParameter("form")) && request.getAttribute("errorMessage") != null) { %>
                        <div class="message error show">
                            ✗ <%= request.getAttribute("errorMessage") %>
                        </div>
                    <% } %>

                    <% if ("login".equals(request.getParameter("form")) && request.getAttribute("successMessage") != null) { %>
                        <div class="message success show">
                            ✓ <%= request.getAttribute("successMessage") %>
                        </div>
                    <% } %>

                    <!-- Login Form -->
                    <form action="login" method="post" id="loginForm">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                        <div class="form-group">
                            <label for="loginUsername">Username</label>
                            <input type="text" name="username" id="loginUsername" 
                                placeholder="Enter your username" required>
                        </div>

                        <div class="form-group">
                            <label for="loginPassword">Password</label>
                            <div class="input-wrapper">
                                <input type="password" name="password" id="loginPassword" 
                                    placeholder="Enter your password" required>
                                <span class="password-toggle" id="toggleLoginPassword">👁️</span>
                            </div>
                        </div>

                        <div class="form-footer">
                            <label class="remember-me">
                                <input type="checkbox" name="remember">
                                <span>Remember me</span>
                            </label>
                            <a href="forgot-password.jsp" class="forgot-link">Forgot Password?</a>
                        </div>

                        <button type="submit" class="submit-btn" name="action" value="loginBtn">
                            Login
                        </button>
                    </form>

                    <div class="divider">or Sign with Google</div>

                    <!-- Google Sign-In Button for Login -->
                    <div class="social-login">
                        <div id="g_id_onload_login"
                            data-client_id="${googleClientId}"
                            data-context="signin"
                            data-ux_mode="popup"
                            data-callback="handleCredentialResponse"
                            data-auto_prompt="false">
                        </div>
                        
                        <div class="g_id_signin"
                            data-type="standard"
                            data-shape="rectangular"
                            data-theme="outline"
                            data-text="signin_with"
                            data-size="large"
                            data-logo_alignment="left"
                            data-width="100%">
                        </div>
                    </div>

                    <div class="switch-link">
                        Not registered yet? <a onclick="switchToRegister()">Create an Account</a>
                    </div>
                </div>

                <!-- REGISTER FORM -->
                <div class="register-form">
                    <div class="form-header">
                        <h1>📝 Create Account</h1>
                        <p>Create a new account to get started!</p>
                    </div>

                    <!-- Register Messages -->
                    <% if ("register".equals(request.getParameter("form")) && request.getAttribute("successMessage") != null) { %>
                        <div class="message success show">
                            ✓ <%= request.getAttribute("successMessage") %>
                        </div>
                    <% } %>

                    <% if ("register".equals(request.getParameter("form")) && request.getAttribute("errorMessage") != null) { %>
                        <div class="message error show">
                            ✗ <%= request.getAttribute("errorMessage") %>
                        </div>
                    <% } %>

                    <form action="register" method="post" id="registerForm">
                        <div class="form-group">
                            <label for="registerUsername">Username</label>
                            <input type="text" name="username" id="registerUsername" 
                                placeholder="Select a username" required minlength="3">
                            <div class="password-hint">Minimum 3 characters</div>
                        </div>

                        <div class="form-group">
                            <label for="registerEmail">Email</label>
                            <input type="email" name="email" id="registerEmail" 
                                placeholder="email@example.com" required>
                        </div>

                        <div class="form-group">
                            <label for="registerPassword">Password</label>
                            <div class="input-wrapper">
                                <input type="password" name="password" id="registerPassword" 
                                    placeholder="Create strong passwords" required minlength="6">
                                <span class="password-toggle" id="toggleRegisterPassword">👁️</span>
                            </div>
                            <div class="password-strength" id="passwordStrength">
                                <div class="password-strength-bar" id="strengthBar"></div>
                            </div>
                            <div class="password-hint">Minimum 6 characters</div>
                        </div>

                        <div class="form-group">
                            <label for="confirmPassword">Re-Enter password</label>
                            <div class="input-wrapper">
                                <input type="password" name="confirmPassword" id="confirmPassword" 
                                    placeholder="Re-enter password" required minlength="6">
                                <span class="password-toggle" id="toggleConfirmPassword">👁️</span>
                            </div>
                            <div class="password-match" id="passwordMatch"></div>
                        </div>

                        <button type="submit" class="submit-btn">Register</button>
                    </form>

                    <div class="divider">or Sign with Google</div>

                    <!-- Google Sign-In Button for Register -->
                    <div class="social-login">
                        <div id="g_id_onload_register"
                            data-client_id="${googleClientId}"
                            data-context="signin"
                            data-ux_mode="popup"
                            data-callback="handleCredentialResponse"
                            data-auto_prompt="false">
                        </div>
                        
                        <div class="g_id_signin"
                            data-type="standard"
                            data-shape="rectangular"
                            data-theme="outline"
                            data-text="signin_with"
                            data-size="large"
                            data-logo_alignment="left"
                            data-width="100%">
                        </div>
                    </div>

                    <div class="switch-link">
                        Already have an account? <a onclick="switchToLogin()">Back to Login</a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        const authContainer = document.getElementById('authContainer');

        // Switch to Register
        function switchToRegister() {
            authContainer.classList.add('register-mode');
            document.getElementById('registerUsername').focus();
        }

        // Switch to Login
        function switchToLogin() {
            authContainer.classList.remove('register-mode');
            document.getElementById('loginUsername').focus();
        }

        // Check URL parameter on page load
        window.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('form') === 'register') {
                switchToRegister();
            }
        });

        // LOGIN PASSWORD TOGGLE
        const toggleLoginPassword = document.getElementById('toggleLoginPassword');
        const loginPasswordInput = document.getElementById('loginPassword');

        toggleLoginPassword.addEventListener('click', function() {
            const type = loginPasswordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            loginPasswordInput.setAttribute('type', type);
            this.textContent = type === 'password' ? '👁️' : '🙈';
        });

        // REGISTER PASSWORD TOGGLES
        const toggleRegisterPassword = document.getElementById('toggleRegisterPassword');
        const registerPasswordInput = document.getElementById('registerPassword');

        toggleRegisterPassword.addEventListener('click', function() {
            const type = registerPasswordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            registerPasswordInput.setAttribute('type', type);
            this.textContent = type === 'password' ? '👁️' : '🙈';
        });

        const toggleConfirmPassword = document.getElementById('toggleConfirmPassword');
        const confirmPasswordInput = document.getElementById('confirmPassword');

        toggleConfirmPassword.addEventListener('click', function() {
            const type = confirmPasswordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            confirmPasswordInput.setAttribute('type', type);
            this.textContent = type === 'password' ? '👁️' : '🙈';
        });

        // PASSWORD STRENGTH CHECKER
        const strengthIndicator = document.getElementById('passwordStrength');
        const strengthBar = document.getElementById('strengthBar');

        registerPasswordInput.addEventListener('input', function() {
            const password = this.value;
            
            if (password.length === 0) {
                strengthIndicator.classList.remove('show');
                return;
            }

            strengthIndicator.classList.add('show');
            
            let strength = 0;
            if (password.length >= 6) strength++;
            if (password.length >= 10) strength++;
            if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
            if (/\d/.test(password)) strength++;
            if (/[^a-zA-Z\d]/.test(password)) strength++;

            strengthBar.className = 'password-strength-bar';
            
            if (strength <= 2) {
                strengthBar.classList.add('strength-weak');
            } else if (strength <= 3) {
                strengthBar.classList.add('strength-medium');
            } else {
                strengthBar.classList.add('strength-strong');
            }

            checkPasswordMatch();
        });

        // PASSWORD MATCH CHECKER
        const passwordMatchIndicator = document.getElementById('passwordMatch');

        function checkPasswordMatch() {
            const password = registerPasswordInput.value;
            const confirmPassword = confirmPasswordInput.value;

            if (confirmPassword.length === 0) {
                passwordMatchIndicator.classList.remove('show');
                return;
            }

            passwordMatchIndicator.classList.add('show');

            if (password === confirmPassword) {
                passwordMatchIndicator.textContent = '✓ Passwords match';
                passwordMatchIndicator.className = 'password-match show match';
            } else {
                passwordMatchIndicator.textContent = '✗ Passwords do not match';
                passwordMatchIndicator.className = 'password-match show no-match';
            }
        }

        confirmPasswordInput.addEventListener('input', checkPasswordMatch);

        // LOGIN FORM VALIDATION
        const loginForm = document.getElementById('loginForm');
        const loginBtn = loginForm.querySelector('.submit-btn');

        loginForm.addEventListener('submit', function(e) {
            const username = document.getElementById('loginUsername').value.trim();
            const password = loginPasswordInput.value;

            if (!username || !password) {
                e.preventDefault();
                alert('Please fill in all fields!');
                return;
            }

            loginBtn.textContent = 'Logging in...';
            loginBtn.disabled = true;
        });

        // REGISTER FORM VALIDATION
        const registerForm = document.getElementById('registerForm');
        const registerBtn = registerForm.querySelector('.submit-btn');

        registerForm.addEventListener('submit', function(e) {
            const username = document.getElementById('registerUsername').value;
            const email = document.getElementById('registerEmail').value;
            const password = registerPasswordInput.value;
            const confirmPassword = confirmPasswordInput.value;

            if (username.length < 3) {
                e.preventDefault();
                alert('Username must be at least 3 characters!');
                return;
            }

            if (!email.includes('@')) {
                e.preventDefault();
                alert('Invalid email!');
                return;
            }

            if (password.length < 6) {
                e.preventDefault();
                alert('Password must be at least 6 characters!');
                return;
            }

            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Passwords do not match! Please check again.');
                return;
            }

            registerBtn.textContent = 'Registering...';
            registerBtn.disabled = true;
        });

        // GOOGLE SIGN-IN CALLBACK
        function handleCredentialResponse(response) {
            console.log("Encoded JWT ID token: " + response.credential);
            
            const googleBtns = document.querySelectorAll('.g_id_signin');
            googleBtns.forEach(btn => {
                btn.style.opacity = '0.5';
                btn.style.pointerEvents = 'none';
            });

            fetch('google-login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'credential=' + encodeURIComponent(response.credential)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showMessage('success', 'Welcome ' + data.user.name + '! Redirecting...');
                    setTimeout(() => {
                        window.location.href = 'home.jsp';
                    }, 1500);
                } else {
                    showMessage('error', data.error || 'Login failed');
                    googleBtns.forEach(btn => {
                        btn.style.opacity = '1';
                        btn.style.pointerEvents = 'auto';
                    });
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showMessage('error', 'Network error. Please try again.');
                googleBtns.forEach(btn => {
                    btn.style.opacity = '1';
                    btn.style.pointerEvents = 'auto';
                });
            });
        }

        // SHOW MESSAGES
        function showMessage(type, message) {
            const messageDiv = document.createElement('div');
            messageDiv.className = 'message ' + type + ' show';
            messageDiv.textContent = (type === 'error' ? '✗ ' : '✓ ') + message;
            
            const activeForm = authContainer.classList.contains('register-mode') 
                ? document.querySelector('.register-form .form-header')
                : document.querySelector('.login-form .form-header');
            
            activeForm.after(messageDiv);
            
            setTimeout(() => {
                messageDiv.style.opacity = '0';
                messageDiv.style.transition = 'opacity 0.5s ease';
                setTimeout(() => messageDiv.remove(), 500);
            }, 5000);
        }

        // AUTO-HIDE MESSAGES
        const messages = document.querySelectorAll('.message.show');
        messages.forEach(message => {
            setTimeout(() => {
                message.style.opacity = '0';
                message.style.transition = 'opacity 0.5s ease';
                setTimeout(() => {
                    message.style.display = 'none';
                }, 500);
            }, 5000);
        });

        // AUTO-FOCUS
        document.getElementById('loginUsername').focus();
    </script>
</body>
</html>