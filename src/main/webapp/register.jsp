<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<meta charset="UTF-8">

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register An Account</title>
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
            left: 20px;
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

        .register-container {
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
        }

        /* Left Side - Image */
        .left-side {
            flex: 0 0 55%;
            position: relative;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
            min-height: 600px;
        }

        .left-side img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            object-position: center;
            display: block;
        }

        /* Right Side - Form */
        .right-side {
            flex: 1;
            padding: clamp(40px, 5vh, 60px) clamp(30px, 4vw, 50px);
            display: flex;
            flex-direction: column;
            justify-content: center;
            overflow-y: auto;
            background: white;
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

        .login-link {
            text-align: center;
            color: #666;
            font-size: 13px;
            margin-top: 15px;
            margin-bottom: 0;
        }

        .login-link a {
            color: #ff6b9d;
            text-decoration: none;
            font-weight: 700;
            transition: color 0.3s ease;
        }

        .login-link a:hover {
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
            gap: 12px;
            margin-bottom: 20px;
        }

        /* Responsive Tablet */
        @media (max-width: 1024px) {
            .register-container {
                max-width: 900px;
            }

            .left-side {
                flex: 0 0 50%;
                min-height: 500px;
            }

            .right-side {
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
                left: 15px;
                padding: 8px 14px;
                font-size: 13px;
                gap: 6px;
            }

            .register-container {
                flex-direction: column;
                max-height: none;
                height: auto;
                width: 100vw;
                max-width: 100vw;
                border-radius: 0;
                margin-top: 0;
                padding-top: 60px;
            }

            .left-side {
                min-height: 250px;
                flex: 0 0 auto;
            }

            .right-side {
                padding: 30px 25px 40px;
            }

            .form-header h1 {
                font-size: 24px;
            }

            .form-header p {
                font-size: 13px;
            }
        }

        /* Extra Small Mobile */
        @media (max-width: 480px) {
            .back-arrow {
                top: 10px;
                left: 10px;
                padding: 7px 12px;
                font-size: 12px;
            }

            .register-container {
                padding-top: 50px;
            }

            .right-side {
                padding: 25px 20px 35px;
            }

            .left-side {
                min-height: 200px;
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
        .right-side::-webkit-scrollbar {
            width: 6px;
        }

        .right-side::-webkit-scrollbar-track {
            background: #f1f1f1;
        }

        .right-side::-webkit-scrollbar-thumb {
            background: #ff6b9d;
            border-radius: 3px;
        }

        .right-side::-webkit-scrollbar-thumb:hover {
            background: #ff4d88;
        }
    </style>
</head>
<body>
    <!-- Back Arrow - Fixed Position -->
    <a href="home.jsp" class="back-arrow">← HOME</a>

    <div class="register-container">
        <!-- Left Side - Image -->
        <div class="left-side">
            <img src="${pageContext.request.contextPath}/resources/img/IMAGE_LOGIN.png" alt="Register Image">
        </div>

        <!-- Right Side - Form -->
        <div class="right-side">
            <div class="form-header">
                <h1>📝 Create Account</h1>
                <p>Create a new account to get started!</p>
            </div>

            <form action="register" method="post" id="registerForm">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" name="username" id="username" placeholder="Select a username" required minlength="3">
                    <div class="password-hint">Minimum 3 characters</div>
                </div>

                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" name="email" id="email" placeholder="email@example.com" required>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <div class="input-wrapper">
                        <input type="password" name="password" id="password" placeholder="Create strong passwords" required minlength="6">
                        <span class="password-toggle" id="togglePassword">👁️</span>
                    </div>
                    <div class="password-strength" id="passwordStrength">
                        <div class="password-strength-bar" id="strengthBar"></div>
                    </div>
                    <div class="password-hint">Minimum 6 characters</div>
                </div>

                <div class="form-group">
                    <label for="confirmPassword">Re-enter password</label>
                    <div class="input-wrapper">
                        <input type="password" name="confirmPassword" id="confirmPassword" placeholder="Re-enter password" required minlength="6">
                        <span class="password-toggle" id="toggleConfirmPassword">👁️</span>
                    </div>
                    <div class="password-match" id="passwordMatch"></div>
                </div>

                <button type="submit" class="submit-btn">Register</button>
            </form>

            <% if (request.getAttribute("successMessage") != null) { %>
                <div class="message success show">
                    ✓ <%= request.getAttribute("successMessage") %>
                </div>
            <% } %>

            <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="message error show">
                    ✗ <%= request.getAttribute("errorMessage") %>
                </div>
            <% } %>

            <div class="divider">or Sign with Email</div>

            <div class="social-login">
                <button type="button" class="social-btn">
                    <svg class="google-icon" viewBox="0 0 24 24">
                        <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                        <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                        <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                        <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                    </svg>
                    Sign in with Google
                </button>
            </div>

            <div class="login-link">
                Already have an account? <a href="login.jsp">Back to login</a>
            </div>
        </div>
    </div>

    <script>
        // Toggle password visibility for password field
        const togglePassword = document.getElementById('togglePassword');
        const passwordInput = document.getElementById('password');

        togglePassword.addEventListener('click', function() {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            this.textContent = type === 'password' ? '👁️' : '🙈';
        });

        // Toggle password visibility for confirm password field
        const toggleConfirmPassword = document.getElementById('toggleConfirmPassword');
        const confirmPasswordInput = document.getElementById('confirmPassword');

        toggleConfirmPassword.addEventListener('click', function() {
            const type = confirmPasswordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            confirmPasswordInput.setAttribute('type', type);
            this.textContent = type === 'password' ? '👁️' : '🙈';
        });

        // Password strength checker
        const strengthIndicator = document.getElementById('passwordStrength');
        const strengthBar = document.getElementById('strengthBar');

        passwordInput.addEventListener('input', function() {
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

            // Check password match when password changes
            checkPasswordMatch();
        });

        // Password match checker
        const passwordMatchIndicator = document.getElementById('passwordMatch');

        function checkPasswordMatch() {
            const password = passwordInput.value;
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

        // Form validation
        const registerForm = document.getElementById('registerForm');
        const submitBtn = document.querySelector('.submit-btn');

        registerForm.addEventListener('submit', function(e) {
            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = passwordInput.value;
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

            submitBtn.textContent = 'Registering...';
            submitBtn.disabled = true;
        });

        // Auto-hide messages after 5 seconds
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

        // Auto-focus first input
        document.getElementById('username').focus();
    </script>
</body>
</html>