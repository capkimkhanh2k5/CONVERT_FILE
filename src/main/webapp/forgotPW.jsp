<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password</title>

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

        .forgot-container {
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

        .step-indicator {
            display: flex;
            justify-content: space-between;
            margin-bottom: 30px;
            position: relative;
        }

        .step-indicator::before {
            content: '';
            position: absolute;
            top: 20px;
            left: 0;
            right: 0;
            height: 2px;
            background: #e8ebf0;
            z-index: 0;
        }

        .step {
            display: flex;
            flex-direction: column;
            align-items: center;
            position: relative;
            z-index: 1;
            flex: 1;
        }

        .step-number {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: #e8ebf0;
            color: #999;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            font-size: 16px;
            margin-bottom: 8px;
            transition: all 0.3s ease;
        }

        .step.active .step-number {
            background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
            color: white;
            box-shadow: 0 4px 12px rgba(255, 107, 157, 0.3);
        }

        .step.completed .step-number {
            background: #4caf50;
            color: white;
        }

        .step-label {
            font-size: 12px;
            color: #999;
            font-weight: 600;
            text-align: center;
        }

        .step.active .step-label {
            color: #ff6b9d;
        }

        .step.completed .step-label {
            color: #4caf50;
        }

        .form-step {
            display: none;
        }

        .form-step.active {
            display: block;
            animation: fadeIn 0.4s ease;
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
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
        input[type="email"],
        input[type="password"] {
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

        .verification-input {
            display: flex;
            gap: 10px;
            justify-content: center;
            margin: 20px 0;
        }

        .verification-input input {
            width: 50px;
            height: 55px;
            text-align: center;
            font-size: 24px;
            font-weight: 700;
            border: 2px solid #e8ebf0;
            border-radius: 10px;
            background: #f8f9fa;
        }

        .verification-input input:focus {
            border-color: #ff6b9d;
            background: white;
            box-shadow: 0 0 0 4px rgba(255, 107, 157, 0.1);
        }

        .btn {
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

        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 28px rgba(255, 107, 157, 0.4);
        }

        .btn:active {
            transform: translateY(0);
        }

        .btn:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }

        .btn-secondary {
            background: #f8f9fa;
            color: #666;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            margin-top: 10px;
        }

        .btn-secondary:hover {
            background: #e8ebf0;
        }

        .error-message,
        .success-message,
        .info-message {
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

        .info-message {
            background: #e3f2fd;
            color: #1565c0;
            border: 2px solid #bbdefb;
        }

        .error-message.show,
        .success-message.show,
        .info-message.show {
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

        .resend-code {
            text-align: center;
            margin-top: 15px;
            font-size: 13px;
            color: #666;
        }

        .resend-code button {
            background: none;
            border: none;
            color: #ff6b9d;
            font-weight: 600;
            cursor: pointer;
            text-decoration: underline;
            padding: 0;
            font-size: 13px;
        }

        .resend-code button:hover {
            color: #ff4d88;
        }

        .resend-code button:disabled {
            color: #999;
            cursor: not-allowed;
            text-decoration: none;
        }

        .timer {
            color: #ff6b9d;
            font-weight: 700;
        }

        .back-to-login {
            text-align: center;
            color: #666;
            font-size: 13px;
            margin-top: 20px;
        }

        .back-to-login a {
            color: #ff6b9d;
            text-decoration: none;
            font-weight: 700;
            transition: color 0.3s ease;
        }

        .back-to-login a:hover {
            color: #ff4d88;
            text-decoration: underline;
        }

        .password-requirements {
            margin-top: 10px;
            padding: 12px;
            background: #f8f9fa;
            border-radius: 8px;
            font-size: 12px;
        }

        .password-requirements ul {
            margin: 8px 0 0 0;
            padding-left: 20px;
            color: #666;
        }

        .password-requirements li {
            margin: 4px 0;
        }

        .password-requirements li.valid {
            color: #4caf50;
        }

        .password-requirements li.invalid {
            color: #c62828;
        }

        /* Responsive Tablet */
        @media (max-width: 1024px) {
            .forgot-container {
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

            .forgot-container {
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

            .step-label {
                font-size: 10px;
            }

            .step-number {
                width: 35px;
                height: 35px;
                font-size: 14px;
            }

            .verification-input input {
                width: 45px;
                height: 50px;
                font-size: 20px;
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

            .forgot-container {
                padding-top: 50px;
            }

            .right-side {
                padding: 25px 20px 35px;
            }

            .left-side {
                min-height: 200px;
            }

            .verification-input input {
                width: 40px;
                height: 45px;
                font-size: 18px;
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
    <!-- Back Arrow -->
    <a href="login.jsp" class="back-arrow">← BACK TO LOGIN</a>

    <div class="forgot-container">
        <!-- Left Side - Image -->
        <div class="left-side">
            <img src="${pageContext.request.contextPath}/resources/img/IMAGE_LOGIN.png" alt="Forgot Password Image">
        </div>

        <!-- Right Side - Form -->
        <div class="right-side">
            <div class="form-header">
                <h1>🔒 Forgot Password?</h1>
                <p>Don't worry! We'll help you reset it</p>
            </div>

            <!-- Step Indicator -->
            <div class="step-indicator">
                <div class="step active" id="step1Indicator">
                    <div class="step-number">1</div>
                    <div class="step-label">Enter Email</div>
                </div>
                <div class="step" id="step2Indicator">
                    <div class="step-number">2</div>
                    <div class="step-label">Verify Code</div>
                </div>
                <div class="step" id="step3Indicator">
                    <div class="step-number">3</div>
                    <div class="step-label">New Password</div>
                </div>
            </div>

            <!-- Messages -->
            <div id="messageContainer"></div>

            <!-- Step 1: Enter Email -->
            <div class="form-step active" id="step1">
                <div class="info-message show">
                    ℹ️ Enter your email address and we'll send you a verification code
                </div>

                <form id="emailForm">
                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" name="email" id="email" 
                            placeholder="Enter your registered email" required>
                    </div>

                    <button type="submit" class="btn" id="sendCodeBtn">
                        Send Verification Code
                    </button>
                </form>

                <div class="back-to-login">
                    Remember your password? <a href="login.jsp">Login here</a>
                </div>
            </div>

            <!-- Step 2: Verify Code -->
            <div class="form-step" id="step2">
                <div class="info-message show">
                    ℹ️ Enter the 6-digit code sent to your email
                </div>

                <form id="verifyForm">
                    <div class="form-group">
                        <label>Verification Code</label>
                        <div class="verification-input">
                            <input type="text" maxlength="1" class="code-input" id="code1" required>
                            <input type="text" maxlength="1" class="code-input" id="code2" required>
                            <input type="text" maxlength="1" class="code-input" id="code3" required>
                            <input type="text" maxlength="1" class="code-input" id="code4" required>
                            <input type="text" maxlength="1" class="code-input" id="code5" required>
                            <input type="text" maxlength="1" class="code-input" id="code6" required>
                        </div>
                    </div>

                    <button type="submit" class="btn" id="verifyCodeBtn">
                        Verify Code
                    </button>

                    <div class="resend-code">
                        Didn't receive code? 
                        <button type="button" id="resendBtn" disabled>
                            Resend (<span class="timer" id="timer">60</span>s)
                        </button>
                    </div>
                </form>

                <button type="button" class="btn btn-secondary" id="backToStep1">
                    ← Back
                </button>
            </div>

            <!-- Step 3: Reset Password -->
            <div class="form-step" id="step3">
                <div class="info-message show">
                    ℹ️ Create a strong new password
                </div>

                <form id="resetPasswordForm">
                    <div class="form-group">
                        <label for="newPassword">New Password</label>
                        <div class="input-wrapper">
                            <input type="password" name="newPassword" id="newPassword" 
                                placeholder="Enter new password" required>
                            <span class="password-toggle" id="toggleNewPassword">👁️</span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password</label>
                        <div class="input-wrapper">
                            <input type="password" name="confirmPassword" id="confirmPassword" 
                                placeholder="Confirm new password" required>
                            <span class="password-toggle" id="toggleConfirmPassword">👁️</span>
                        </div>
                    </div>

                    <div class="password-requirements">
                        <strong>Password must contain:</strong>
                        <ul id="passwordChecks">
                            <li id="check-length">At least 8 characters</li>
                            <li id="check-uppercase">One uppercase letter</li>
                            <li id="check-lowercase">One lowercase letter</li>
                            <li id="check-number">One number</li>
                        </ul>
                    </div>

                    <button type="submit" class="btn" id="resetPasswordBtn">
                        Reset Password
                    </button>
                </form>
            </div>
        </div>
    </div>

    <script>
        // Global variables
        let currentStep = 1;
        let userEmail = '';
        let verificationCode = '';
        let resendTimer = 60;
        let timerInterval;

        // Step navigation
        function showStep(step) {
            // Hide all steps
            document.querySelectorAll('.form-step').forEach(s => s.classList.remove('active'));
            document.querySelectorAll('.step').forEach(s => {
                s.classList.remove('active');
                s.classList.remove('completed');
            });

            // Show current step
            document.getElementById('step' + step).classList.add('active');
            document.getElementById('step' + step + 'Indicator').classList.add('active');

            // Mark completed steps
            for (let i = 1; i < step; i++) {
                document.getElementById('step' + i + 'Indicator').classList.add('completed');
            }

            currentStep = step;
        }

        // Show message
        function showMessage(type, message) {
            const messageContainer = document.getElementById('messageContainer');
            const messageDiv = document.createElement('div');
            messageDiv.className = `${type}-message show`;
            messageDiv.textContent = (type === 'error' ? '✗ ' : type === 'success' ? '✓ ' : 'ℹ️ ') + message;
            
            messageContainer.innerHTML = '';
            messageContainer.appendChild(messageDiv);
            
            setTimeout(() => {
                messageDiv.style.opacity = '0';
                messageDiv.style.transition = 'opacity 0.5s ease';
                setTimeout(() => messageDiv.remove(), 500);
            }, 5000);
        }

        // STEP 1: Send Verification Code
        document.getElementById('emailForm').addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const email = document.getElementById('email').value.trim();
            const sendBtn = document.getElementById('sendCodeBtn');
            
            if (!email) {
                showMessage('error', 'Please enter your email address');
                return;
            }

            // Validate email format
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                showMessage('error', 'Please enter a valid email address');
                return;
            }

            sendBtn.textContent = 'Sending code...';
            sendBtn.disabled = true;

            try {
                // Call API to send verification code
                const response = await fetch('forgot-password', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'action=sendCode&email=' + encodeURIComponent(email)
                });

                const data = await response.json();

                if (data.success) {
                    userEmail = email;
                    showMessage('success', 'Verification code sent to your email!');
                    setTimeout(() => {
                        showStep(2);
                        startResendTimer();
                        document.getElementById('code1').focus();
                    }, 1500);
                } else {
                    showMessage('error', data.message || 'Email not found');
                    sendBtn.textContent = 'Send Verification Code';
                    sendBtn.disabled = false;
                }
            } catch (error) {
                console.error('Error:', error);
                showMessage('error', 'Network error. Please try again.');
                sendBtn.textContent = 'Send Verification Code';
                sendBtn.disabled = false;
            }
        });

        // STEP 2: Verify Code
        // Auto-focus next input
        const codeInputs = document.querySelectorAll('.code-input');
        codeInputs.forEach((input, index) => {
            input.addEventListener('input', function(e) {
                if (this.value.length === 1 && index < codeInputs.length - 1) {
                    codeInputs[index + 1].focus();
                }
            });

            input.addEventListener('keydown', function(e) {
                if (e.key === 'Backspace' && this.value === '' && index > 0) {
                    codeInputs[index - 1].focus();
                }
            });

            // Only allow numbers
            input.addEventListener('keypress', function(e) {
                if (!/[0-9]/.test(e.key)) {
                    e.preventDefault();
                }
            });
        });

        document.getElementById('verifyForm').addEventListener('submit', async function(e) {
            e.preventDefault();

            let code = '';
            codeInputs.forEach(input => code += input.value);

            if (code.length !== 6) {
                showMessage('error', 'Please enter complete 6-digit code');
                return;
            }

            const verifyBtn = document.getElementById('verifyCodeBtn');
            verifyBtn.textContent = 'Verifying...';
            verifyBtn.disabled = true;

            try {
                const response = await fetch('forgot-password', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `action=verifyCode&email=${encodeURIComponent(userEmail)}&code=${code}`
                });

                const data = await response.json();

                if (data.success) {
                    verificationCode = code;
                    showMessage('success', 'Code verified successfully!');
                    clearInterval(timerInterval);
                    setTimeout(() => {
                        showStep(3);
                        document.getElementById('newPassword').focus();
                    }, 1500);
                } else {
                    showMessage('error', data.message || 'Invalid verification code');
                    verifyBtn.textContent = 'Verify Code';
                    verifyBtn.disabled = false;
                    codeInputs.forEach(input => input.value = '');
                    codeInputs[0].focus();
                }
            } catch (error) {
                console.error('Error:', error);
                showMessage('error', 'Network error. Please try again.');
                verifyBtn.textContent = 'Verify Code';
                verifyBtn.disabled = false;
            }
        });

        // Resend code
        function startResendTimer() {
            resendTimer = 60;
            document.getElementById('resendBtn').disabled = true;
            
            timerInterval = setInterval(() => {
                resendTimer--;
                document.getElementById('timer').textContent = resendTimer;
                
                if (resendTimer <= 0) {
                    clearInterval(timerInterval);
                    document.getElementById('resendBtn').disabled = false;
                    document.getElementById('resendBtn').innerHTML = 'Resend code';
                }
            }, 1000);
        }

        document.getElementById('resendBtn').addEventListener('click', async function() {
            this.disabled = true;
            
            try {
                const response = await fetch('forgot-password', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'action=sendCode&email=' + encodeURIComponent(userEmail)
                });

                const data = await response.json();

                if (data.success) {
                    showMessage('success', 'New code sent to your email!');
                    startResendTimer();
                    codeInputs.forEach(input => input.value = '');
                    codeInputs[0].focus();
                } else {
                    showMessage('error', 'Failed to resend code. Please try again.');
                    this.disabled = false;
                }
            } catch (error) {
                console.error('Error:', error);
                showMessage('error', 'Network error. Please try again.');
                this.disabled = false;
            }
        });

        // Back to step 1
        document.getElementById('backToStep1').addEventListener('click', function() {
            clearInterval(timerInterval);
            codeInputs.forEach(input => input.value = '');
            showStep(1);
            document.getElementById('email').focus();
        });

        // STEP 3: Reset Password
        // Password toggle
        document.getElementById('toggleNewPassword').addEventListener('click', function() {
            const input = document.getElementById('newPassword');
            const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
            input.setAttribute('type', type);
            this.textContent = type === 'password' ? '👁️' : '🙈';
        });

        document.getElementById('toggleConfirmPassword').addEventListener('click', function() {
            const input = document.getElementById('confirmPassword');
            const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
            input.setAttribute('type', type);
            this.textContent = type === 'password' ? '👁️' : '🙈';
        });

        // Password validation
        const passwordChecks = {
            length: false,
            uppercase: false,
            lowercase: false,
            number: false
        };

        document.getElementById('newPassword').addEventListener('input', function() {
            const password = this.value;

            // Check length
            passwordChecks.length = password.length >= 8;
            document.getElementById('check-length').className = passwordChecks.length ? 'valid' : 'invalid';

            // Check uppercase
            passwordChecks.uppercase = /[A-Z]/.test(password);
            document.getElementById('check-uppercase').className = passwordChecks.uppercase ? 'valid' : 'invalid';

            // Check lowercase
            passwordChecks.lowercase = /[a-z]/.test(password);
            document.getElementById('check-lowercase').className = passwordChecks.lowercase ? 'valid' : 'invalid';

            // Check number
            passwordChecks.number = /[0-9]/.test(password);
            document.getElementById('check-number').className = passwordChecks.number ? 'valid' : 'invalid';
        });

        // Reset password form
        document.getElementById('resetPasswordForm').addEventListener('submit', async function(e) {
            e.preventDefault();

            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            const resetBtn = document.getElementById('resetPasswordBtn');

            // Validate all password requirements
            if (!Object.values(passwordChecks).every(check => check)) {
                showMessage('error', 'Password does not meet all requirements');
                return;
            }

            // Check if passwords match
            if (newPassword !== confirmPassword) {
                showMessage('error', 'Passwords do not match');
                document.getElementById('confirmPassword').focus();
                return;
            }

            resetBtn.textContent = 'Resetting password...';
            resetBtn.disabled = true;

            try {
                const response = await fetch('forgot-password', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `action=resetPassword&email=${encodeURIComponent(userEmail)}&code=${verificationCode}&newPassword=${encodeURIComponent(newPassword)}`
                });

                const data = await response.json();

                if (data.success) {
                    showMessage('success', 'Password reset successfully! Redirecting to login...');
                    setTimeout(() => {
                        window.location.href = 'login.jsp';
                    }, 2000);
                } else {
                    showMessage('error', data.message || 'Failed to reset password');
                    resetBtn.textContent = 'Reset Password';
                    resetBtn.disabled = false;
                }
            } catch (error) {
                console.error('Error:', error);
                showMessage('error', 'Network error. Please try again.');
                resetBtn.textContent = 'Reset Password';
                resetBtn.disabled = false;
            }
        });

        // Auto-focus on page load
        document.getElementById('email').focus();

        // Prevent back button after successful reset
        window.history.pushState(null, null, window.location.href);
        window.onpopstate = function () {
            window.history.go(1);
        };
    </script>
</body>
</html>