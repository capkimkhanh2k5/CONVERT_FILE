<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<meta charset="UTF-8">
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register An Account - CONVERT_FILE</title>
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
            max-width: 500px;
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

        .form-group {
            margin-bottom: 25px;
        }

        label {
            display: block;
            color: #333;
            font-weight: 600;
            margin-bottom: 10px;
            font-size: 14px;
        }

        .input-wrapper {
            position: relative;
        }

        .input-icon {
            position: absolute;
            left: 15px;
            top: 50%;
            transform: translateY(-50%);
            font-size: 20px;
            color: #667eea;
        }

        input[type="text"],
        input[type="password"],
        input[type="email"] {
            width: 100%;
            padding: 15px 15px 15px 50px;
            border: 2px solid #e0e0e0;
            border-radius: 12px;
            font-size: 15px;
            transition: all 0.3s ease;
            background: #f5f7fa;
        }

        input[type="text"]:focus,
        input[type="password"]:focus,
        input[type="email"]:focus {
            outline: none;
            border-color: #667eea;
            background: white;
            box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
        }

        input[type="text"]:hover,
        input[type="password"]:hover,
        input[type="email"]:hover {
            border-color: #764ba2;
        }

        .password-toggle {
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-50%);
            cursor: pointer;
            font-size: 20px;
            color: #999;
            transition: color 0.3s ease;
            user-select: none;
        }

        .password-toggle:hover {
            color: #667eea;
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
            font-size: 12px;
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
            padding: 16px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            margin-top: 10px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        }

        .submit-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
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
            margin-top: 20px;
            padding: 15px;
            border-radius: 12px;
            text-align: center;
            font-weight: 500;
            display: none;
            animation: slideDown 0.3s ease;
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

        .message.show {
            display: block;
        }

        .message.success {
            background: #e8f5e9;
            color: #2e7d32;
            border: 2px solid #4caf50;
        }

        .message.error {
            background: #ffebee;
            color: #c62828;
            border: 2px solid #f44336;
            animation: shake 0.5s ease;
        }

        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
            20%, 40%, 60%, 80% { transform: translateX(5px); }
        }

        .divider {
            text-align: center;
            margin: 30px 0;
            color: #999;
            position: relative;
        }

        .divider::before,
        .divider::after {
            content: '';
            position: absolute;
            top: 50%;
            width: 40%;
            height: 1px;
            background: #e0e0e0;
        }

        .divider::before {
            left: 0;
        }

        .divider::after {
            right: 0;
        }

        .login-link {
            text-align: center;
            margin-top: 25px;
            color: #666;
            font-size: 14px;
        }

        .login-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: 600;
            transition: color 0.3s ease;
            display: inline-flex;
            align-items: center;
            gap: 5px;
        }

        .login-link a:hover {
            color: #764ba2;
        }

        .back-home-btn {
            display: inline-block;
            margin-top: 15px;
            padding: 12px 24px;
            background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
            color: #667eea;
            text-decoration: none;
            border-radius: 12px;
            font-size: 14px;
            font-weight: 600;
            border: 2px solid #e0e0e0;
            transition: all 0.3s ease;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        .back-home-btn:hover {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-color: #667eea;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
        }

        .back-home-btn::before {
            content: '← ';
            margin-right: 5px;
        }

        @media (max-width: 600px) {
            .container {
                padding: 30px 20px;
            }

            h2 {
                font-size: 24px;
            }
        }
    </style>
</head>
    <body>
        <div class="container">
            <div class="logo">
                <div class="logo-icon">📝</div>
                <h2>Register An Account</h2>
                <p class="subtitle">Create a new account to get started</p>
            </div>

            <form action="register" method="post" id="registerForm">
                <div class="form-group">
                    <label for="username">Username</label>
                    <div class="input-wrapper">
                        <span class="input-icon">👤</span>
                        <input type="text" name="username" id="username" placeholder="Select a username" required minlength="3">
                    </div>
                    <div class="password-hint">Minimum 3 characters</div>
                </div>

                <div class="form-group">
                    <label for="email">Email</label>
                    <div class="input-wrapper">
                        <span class="input-icon">📧</span>
                        <input type="email" name="email" id="email" placeholder="email@example.com" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <div class="input-wrapper">
                        <span class="input-icon">🔒</span>
                        <input type="password" name="password" id="password" placeholder="Create strong passwords" required minlength="6">
                        <span class="password-toggle" id="togglePassword">👁️</span>
                    </div>
                    <div class="password-strength" id="passwordStrength">
                        <div class="password-strength-bar" id="strengthBar"></div>
                    </div>
                    <div class="password-hint">Minimum 6 characters, should have uppercase letters, numbers and special characters</div>
                </div>

                <div class="form-group">
                    <label for="confirmPassword">Re-enter password</label>
                    <div class="input-wrapper">
                        <span class="input-icon">🔒</span>
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

            <div class="divider">or</div>

            <div class="login-link">
                Already have an account? <a href="login.jsp">Back to login</a>
            </div>

            <div style="text-align: center;">
                <a href="upload.jsp" class="back-home-btn">
                    Return to Home page (Guest)
                </a>
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
                    passwordMatchIndicator.textContent = '✓ Password matches';
                    passwordMatchIndicator.className = 'password-match show match';
                } else {
                    passwordMatchIndicator.textContent = '✗ Password no-matches';
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

                if (username.length < 5) {
                    e.preventDefault();
                    alert('Username must be at least 5 characters!');
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