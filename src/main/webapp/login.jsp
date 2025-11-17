<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>

    <!-- Google Sign-In Library -->
    <script src="https://accounts.google.com/gsi/client" async defer></script>

    <style>
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
        }

        /* Back Arrow - Fixed Position */
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

        .login-container {
            display: flex;
            background: white;
            border-radius: clamp(16px, 2vw, 24px);
            overflow: hidden;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
            width: 95vw;
            max-width: 1400px;
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
            margin-bottom: 20px;
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

        .login-btn {
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
            margin-top: 5px;
        }

        .login-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 28px rgba(255, 107, 157, 0.4);
        }

        .login-btn:active {
            transform: translateY(0);
        }

        .login-btn:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }

        .error-message,
        .success-message {
            padding: 14px 18px;
            border-radius: 10px;
            margin-bottom: 20px;
            font-size: 13px;
            font-weight: 500;
            display: none;
        }

        .error-message {
            background: #ffebee;
            color: #c62828;
            border: 2px solid #ffcdd2;
        }

        .success-message {
            background: #e8f5e9;
            color: #2e7d32;
            border: 2px solid #c8e6c9;
        }

        .error-message.show,
        .success-message.show {
            display: block;
            animation: slideIn 0.3s ease;
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

        .social-login {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 12px;
            margin-bottom: 20px;
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

        .google-btn::before {
            content: '';
            display: inline-block;
            width: 20px;
            height: 20px;
            background: url('google-icon.svg') center/contain no-repeat;
            margin-right: 8px;
        }

        .signup-link {
            text-align: center;
            color: #666;
            font-size: 13px;
            margin-top: 15px;
            margin-bottom: 0;
        }

        .signup-link a {
            color: #ff6b9d;
            text-decoration: none;
            font-weight: 700;
            transition: color 0.3s ease;
        }

        .signup-link a:hover {
            color: #ff4d88;
            text-decoration: underline;
        }

        /* Responsive Tablet */
        @media (max-width: 1024px) {
            .login-container {
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
                right: 15px;
                padding: 8px 14px;
                font-size: 13px;
                gap: 6px;
            }

            .login-container {
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

            .login-container {
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
                margin-bottom: 18px;
            }

            input[type="text"],
            input[type="password"],
            input[type="email"] {
                padding: 12px 16px;
                font-size: 14px;
            }

            .login-btn {
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

    <div class="login-container">
        <!-- Left Side - Image -->
        <div class="left-side">
            <img src="${pageContext.request.contextPath}/resources/img/IMAGE_LOGIN.png" alt="Login Image">
        </div>

        <!-- Right Side - Form -->
        <div class="right-side">
            <div class="form-header">
                <h1>📁 Welcome Back!</h1>
                <p>Login to your account to continue</p>
            </div>

            <!-- Error Message -->
            <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="error-message show">
                    ✗ <%= request.getAttribute("errorMessage") %>
                </div>
            <% } %>

            <!-- Success Message -->
            <% if (request.getAttribute("successMessage") != null) { %>
                <div class="success-message show">
                    ✓ <%= request.getAttribute("successMessage") %>
                </div>
            <% } %>

            <!-- Login Form -->
            <form action="login" method="post" id="loginForm">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" name="username" id="username" 
                        placeholder="Enter your username" required>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <div class="input-wrapper">
                        <input type="password" name="password" id="password" 
                            placeholder="Enter your password" required>
                        <span class="password-toggle" id="togglePassword">👁️</span>
                    </div>
                </div>

                <div class="form-footer">
                    <label class="remember-me">
                        <input type="checkbox" name="remember">
                        <span>Remember me</span>
                    </label>
                    <a href="forgot-password.jsp" class="forgot-link">Forgot Password?</a>
                </div>

                <button type="submit" class="login-btn" name="action" value="loginBtn">
                    Login
                </button>
            </form>

            <div class="divider">or Sign with Google</div>

            <!-- Google Sign-In Button -->
            <div class="social-login">
                <div id="g_id_onload"
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

            <div class="signup-link">
                Not registered yet? <a href="register">Create an Account</a>
            </div>
        </div>
    </div>

    <script>
        //PASSWORD TOGGLE
        const togglePassword = document.getElementById('togglePassword');
        const passwordInput = document.getElementById('password');

        togglePassword.addEventListener('click', function() {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            this.textContent = type === 'password' ? '👁️' : '🙈';
        });

        //FORM SUBMISSION
        const loginForm = document.getElementById('loginForm');
        const loginBtn = document.querySelector('.login-btn');

        loginForm.addEventListener('submit', function(e) {
            const username = document.getElementById('username').value.trim();
            const password = passwordInput.value;

            if (!username || !password) {
                e.preventDefault();
                alert('Please fill in all fields!');
                return;
            }

            loginBtn.textContent = 'Logging in...';
            loginBtn.disabled = true;
        });

        //GOOGLE SIGN-IN CALLBACK
        function handleCredentialResponse(response) {
            console.log("Encoded JWT ID token: " + response.credential);
            
            // Hiển thị loading
            const googleBtn = document.querySelector('.g_id_signin');
            if (googleBtn) {
                googleBtn.style.opacity = '0.5';
                googleBtn.style.pointerEvents = 'none';
            }

            // Gửi token đến server
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
                    // Đăng nhập thành công
                    showMessage('success', 'Welcome ' + data.user.name + '! Redirecting...');
                    setTimeout(() => {
                        window.location.href = 'home.jsp';
                    }, 1500);
                } else {
                    // Đăng nhập thất bại
                    showMessage('error', data.error || 'Login failed');
                    if (googleBtn) {
                        googleBtn.style.opacity = '1';
                        googleBtn.style.pointerEvents = 'auto';
                    }
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showMessage('error', 'Network error. Please try again.');
                if (googleBtn) {
                    googleBtn.style.opacity = '1';
                    googleBtn.style.pointerEvents = 'auto';
                }
            });
        }

        //SHOW MESSAGES
        function showMessage(type, message) {
            const messageDiv = document.createElement('div');
            messageDiv.className = type === 'error' ? 'error-message show' : 'success-message show';
            messageDiv.textContent = (type === 'error' ? '✗ ' : '✓ ') + message;
            
            const formHeader = document.querySelector('.form-header');
            formHeader.after(messageDiv);
            
            setTimeout(() => {
                messageDiv.style.opacity = '0';
                messageDiv.style.transition = 'opacity 0.5s ease';
                setTimeout(() => messageDiv.remove(), 500);
            }, 5000);
        }

        //AUTO-HIDE MESSAGES
        const messages = document.querySelectorAll('.error-message.show, .success-message.show');
        messages.forEach(message => {
            setTimeout(() => {
                message.style.opacity = '0';
                message.style.transition = 'opacity 0.5s ease';
                setTimeout(() => message.style.display = 'none', 500);
            }, 5000);
        });

        //AUTO-FOCUS
        document.getElementById('username').focus();
    </script>
</body>
</html>