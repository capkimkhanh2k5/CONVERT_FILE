<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>

        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Reset Password</title>

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
                    padding: 40px;
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
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

                /* Step Indicator */
                .step-indicator {
                    display: flex;
                    justify-content: space-between;
                    margin-bottom: 40px;
                    position: relative;
                }

                .step-indicator::before {
                    content: '';
                    position: absolute;
                    top: 15px;
                    left: 0;
                    right: 0;
                    height: 2px;
                    background: #e2e8f0;
                    z-index: 0;
                }

                .step {
                    position: relative;
                    z-index: 1;
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    gap: 8px;
                    flex: 1;
                }

                .step-num {
                    width: 32px;
                    height: 32px;
                    border-radius: 50%;
                    background: white;
                    border: 2px solid #e2e8f0;
                    color: #94a3b8;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-weight: 600;
                    font-size: 14px;
                    transition: all 0.3s ease;
                }

                .step.active .step-num {
                    border-color: var(--primary);
                    color: var(--primary);
                    box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
                }

                .step.completed .step-num {
                    background: var(--primary);
                    border-color: var(--primary);
                    color: white;
                }

                .step-label {
                    font-size: 12px;
                    font-weight: 600;
                    color: #94a3b8;
                    transition: all 0.3s ease;
                }

                .step.active .step-label {
                    color: var(--primary);
                }

                .step.completed .step-label {
                    color: var(--primary);
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

                /* Code Input */
                .code-inputs {
                    display: flex;
                    gap: 10px;
                    justify-content: center;
                    margin: 20px 0;
                }

                .code-input {
                    width: 50px;
                    height: 60px;
                    border-radius: 12px;
                    border: 2px solid #e2e8f0;
                    background: #f8fafc;
                    text-align: center;
                    font-size: 24px;
                    font-weight: 700;
                    color: var(--text-main);
                    transition: all 0.3s ease;
                }

                .code-input:focus {
                    outline: none;
                    border-color: var(--primary);
                    background: white;
                    box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
                    transform: translateY(-2px);
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

                .submit-btn:disabled {
                    opacity: 0.7;
                    cursor: not-allowed;
                    transform: none;
                }

                .btn-secondary {
                    background: white;
                    color: var(--text-light);
                    border: 2px solid #e2e8f0;
                    box-shadow: none;
                }

                .btn-secondary:hover {
                    background: #f8fafc;
                    border-color: #cbd5e1;
                    color: var(--text-main);
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
                    display: none;
                }

                .message.show {
                    display: flex;
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

                .message.info {
                    background: #eff6ff;
                    color: #3b82f6;
                    border: 1px solid #dbeafe;
                }

                /* Steps Visibility */
                .form-step {
                    display: none;
                    animation: fadeIn 0.4s ease;
                }

                .form-step.active {
                    display: block;
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

                /* Links */
                .back-link {
                    text-align: center;
                    margin-top: 24px;
                    color: var(--text-light);
                    font-size: 14px;
                }

                .back-link a {
                    color: var(--primary);
                    font-weight: 600;
                    text-decoration: none;
                    transition: color 0.3s ease;
                }

                .back-link a:hover {
                    text-decoration: underline;
                }

                .resend-link {
                    text-align: center;
                    margin-top: 16px;
                    font-size: 14px;
                    color: var(--text-light);
                }

                .resend-btn {
                    background: none;
                    border: none;
                    color: var(--primary);
                    font-weight: 600;
                    cursor: pointer;
                    padding: 0;
                    font-size: 14px;
                }

                .resend-btn:disabled {
                    color: var(--text-light);
                    cursor: not-allowed;
                }

                /* Password Requirements */
                .pw-reqs {
                    margin-top: 12px;
                    padding: 12px;
                    background: #f8fafc;
                    border-radius: 8px;
                    font-size: 12px;
                }

                .pw-reqs ul {
                    margin: 8px 0 0 20px;
                    color: var(--text-light);
                }

                .pw-reqs li.valid {
                    color: #22c55e;
                }

                .pw-reqs li.invalid {
                    color: #ef4444;
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
                        min-height: 150px;
                    }

                    .brand-img {
                        display: none;
                    }

                    .brand-text {
                        display: none;
                    }

                    .brand-logo {
                        font-size: 32px;
                        margin: 0;
                    }

                    .form-side {
                        padding: 30px;
                    }

                    .code-input {
                        width: 40px;
                        height: 50px;
                        font-size: 20px;
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

            <a href="<c:url value='/index.jsp'/>" class="back-btn">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                    stroke-linecap="round" stroke-linejoin="round">
                    <path d="M19 12H5M12 19l-7-7 7-7" />
                </svg>
                Back to Home
            </a>

            <div class="auth-container">
                <!-- Brand Side -->
                <div class="brand-side">
                    <div class="brand-content">
                        <div class="brand-logo">ConvertFile</div>
                        <p class="brand-text">Securely reset your password and get back to your files.</p>
                    </div>
                    <img src="<c:url value='/resources/img/IMAGE_LOGIN.png'/>" alt="Illustration" class="brand-img">
                </div>

                <!-- Form Side -->
                <div class="form-side">
                    <h1 class="form-title">Reset Password</h1>
                    <p class="form-subtitle">Follow the steps to recover your account.</p>

                    <!-- Step Indicator -->
                    <div class="step-indicator">
                        <div class="step active" id="step1Indicator">
                            <div class="step-num">1</div>
                            <div class="step-label">Email</div>
                        </div>
                        <div class="step" id="step2Indicator">
                            <div class="step-num">2</div>
                            <div class="step-label">Verify</div>
                        </div>
                        <div class="step" id="step3Indicator">
                            <div class="step-num">3</div>
                            <div class="step-label">Reset</div>
                        </div>
                    </div>

                    <!-- Messages -->
                    <div id="messageContainer"></div>

                    <!-- STEP 1: Email -->
                    <div class="form-step active" id="step1">
                        <div class="message info show">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <line x1="12" y1="16" x2="12" y2="12"></line>
                                <line x1="12" y1="8" x2="12.01" y2="8"></line>
                            </svg>
                            Enter your email to receive a verification code.
                        </div>

                        <form id="emailForm">
                            <div class="input-group">
                                <input type="email" name="email" id="email" class="input-field" placeholder=" "
                                    required>
                                <label for="email" class="input-label">Email Address</label>
                            </div>
                            <button type="submit" class="submit-btn" id="sendCodeBtn">Send Verification Code</button>
                        </form>

                        <div class="back-link">
                            Remember password? <a href="<c:url value='/login'/>">Sign in</a>
                        </div>
                    </div>

                    <!-- STEP 2: Verify Code -->
                    <div class="form-step" id="step2">
                        <div class="message info show">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <line x1="12" y1="16" x2="12" y2="12"></line>
                                <line x1="12" y1="8" x2="12.01" y2="8"></line>
                            </svg>
                            Enter the 6-digit code sent to your email.
                        </div>

                        <form id="verifyForm">
                            <div class="code-inputs">
                                <input type="text" maxlength="1" class="code-input" id="code1" required>
                                <input type="text" maxlength="1" class="code-input" id="code2" required>
                                <input type="text" maxlength="1" class="code-input" id="code3" required>
                                <input type="text" maxlength="1" class="code-input" id="code4" required>
                                <input type="text" maxlength="1" class="code-input" id="code5" required>
                                <input type="text" maxlength="1" class="code-input" id="code6" required>
                            </div>

                            <button type="submit" class="submit-btn" id="verifyCodeBtn">Verify Code</button>

                            <div class="resend-link">
                                Didn't receive code?
                                <button type="button" class="resend-btn" id="resendBtn" disabled>
                                    Resend (<span id="timer">60</span>s)
                                </button>
                            </div>
                        </form>

                        <button type="button" class="submit-btn btn-secondary" onclick="showStep(1)">Back to
                            Email</button>
                    </div>

                    <!-- STEP 3: New Password -->
                    <div class="form-step" id="step3">
                        <div class="message info show">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <line x1="12" y1="16" x2="12" y2="12"></line>
                                <line x1="12" y1="8" x2="12.01" y2="8"></line>
                            </svg>
                            Create a strong new password.
                        </div>

                        <form id="resetPasswordForm">
                            <div class="input-group">
                                <input type="password" name="newPassword" id="newPassword" class="input-field"
                                    placeholder=" " required>
                                <label for="newPassword" class="input-label">New Password</label>
                            </div>

                            <div class="input-group">
                                <input type="password" name="confirmPassword" id="confirmPassword" class="input-field"
                                    placeholder=" " required>
                                <label for="confirmPassword" class="input-label">Confirm Password</label>
                            </div>

                            <div class="pw-reqs">
                                <strong>Requirements:</strong>
                                <ul id="passwordChecks">
                                    <li id="check-length">At least 6 characters</li>
                                    <li id="check-uppercase">One uppercase letter</li>
                                    <li id="check-lowercase">One lowercase letter</li>
                                    <li id="check-number">One number</li>
                                </ul>
                            </div>

                            <button type="submit" class="submit-btn" id="resetPasswordBtn">Reset Password</button>
                        </form>
                    </div>

                </div>
            </div>

            <script>
                // Global variables
                let currentStep = 1;
                let userEmail = '';
                let resendTimer = 180;
                let timerInterval;

                // Step Navigation
                function showStep(step) {
                    // Hide all steps
                    document.querySelectorAll('.form-step').forEach(s => s.classList.remove('active'));

                    // Reset indicators
                    document.querySelectorAll('.step').forEach(s => {
                        s.classList.remove('active', 'completed');
                    });

                    // Show current step
                    document.getElementById('step' + step).classList.add('active');

                    // Update indicators
                    for (let i = 1; i <= 3; i++) {
                        const indicator = document.getElementById('step' + i + 'Indicator');
                        if (i < step) indicator.classList.add('completed');
                        if (i === step) indicator.classList.add('active');
                    }

                    currentStep = step;
                }

                // Message Handler
                function showMessage(type, text) {
                    const container = document.getElementById('messageContainer');
                    container.innerHTML = `
<div class="message ${type} show">
    <span>${text}</span>
</div>
`;

                    setTimeout(() => {
                        const msg = container.querySelector('.message');
                        if (msg) {
                            msg.style.opacity = '0';
                            setTimeout(() => msg.remove(), 300);
                        }
                    }, 5000);
                }

                // Code Input Auto-Focus
                const codeInputs = document.querySelectorAll('.code-input');
                codeInputs.forEach((input, index) => {
                    input.addEventListener('input', (e) => {
                        if (e.target.value.length === 1) {
                            if (index < codeInputs.length - 1) codeInputs[index + 1].focus();
                        }
                    });

                    input.addEventListener('keydown', (e) => {
                        if (e.key === 'Backspace' && !e.target.value) {
                            if (index > 0) codeInputs[index - 1].focus();
                        }
                    });
                });

                // Timer Logic
                function startResendTimer() {
                    resendTimer = 60;
                    const btn = document.getElementById('resendBtn');
                    const timerSpan = document.getElementById('timer');

                    btn.disabled = true;
                    clearInterval(timerInterval);

                    timerInterval = setInterval(() => {
                        resendTimer--;
                        timerSpan.textContent = resendTimer;

                        if (resendTimer <= 0) {
                            clearInterval(timerInterval);
                            btn.disabled = false;
                            btn.innerHTML = 'Resend Code';
                        }
                    }, 1000);
                }

                // STEP 1: Send Code
                document.getElementById('emailForm').addEventListener('submit', async (e) => {
                    e.preventDefault();
                    const email = document.getElementById('email').value.trim();
                    const btn = document.getElementById('sendCodeBtn');

                    if (!email) return showMessage('error', 'Please enter your email');

                    btn.textContent = 'Sending...';
                    btn.disabled = true;

                    try {
                        const res = await fetch('forgot-password', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: 'action=sendCode&email=' + encodeURIComponent(email)
                        });
                        const data = await res.json();

                        if (data.success) {
                            userEmail = email;
                            showMessage('success', 'Code sent successfully!');
                            showStep(2);
                            startResendTimer();
                            document.getElementById('code1').focus();
                        } else {
                            showMessage('error', data.error || 'Failed to send code');
                        }
                    } catch (err) {
                        showMessage('error', 'Network error occurred');
                    } finally {
                        btn.textContent = 'Send Verification Code';
                        btn.disabled = false;
                    }
                });

                // Resend Button
                document.getElementById('resendBtn').addEventListener('click', async () => {
                    const btn = document.getElementById('resendBtn');
                    btn.disabled = true;
                    btn.textContent = 'Sending...';

                    try {
                        const res = await fetch('forgot-password', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: 'action=sendCode&email=' + encodeURIComponent(userEmail)
                        });
                        const data = await res.json();

                        if (data.success) {
                            showMessage('success', 'New code sent!');
                            startResendTimer();
                        } else {
                            showMessage('error', data.error || 'Failed to resend');
                            btn.disabled = false;
                            btn.textContent = 'Resend Code';
                        }
                    } catch (err) {
                        showMessage('error', 'Network error');
                        btn.disabled = false;
                        btn.textContent = 'Resend Code';
                    }
                });

                // STEP 2: Verify Code
                document.getElementById('verifyForm').addEventListener('submit', async (e) => {
                    e.preventDefault();
                    let code = '';
                    codeInputs.forEach(input => code += input.value);

                    if (code.length !== 6) return showMessage('error', 'Please enter the full 6-digit code');

                    const btn = document.getElementById('verifyCodeBtn');
                    btn.textContent = 'Verifying...';
                    btn.disabled = true;

                    try {
                        const res = await fetch('forgot-password', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: 'action=verifyCode&email=' + encodeURIComponent(userEmail) + '&code=' + code
                        });
                        const data = await res.json();

                        if (data.success) {
                            showMessage('success', 'Code verified!');
                            showStep(3);
                        } else {
                            showMessage('error', data.error || 'Invalid code');
                        }
                    } catch (err) {
                        showMessage('error', 'Network error');
                    } finally {
                        btn.textContent = 'Verify Code';
                        btn.disabled = false;
                    }
                });

                // Password Validation
                const newPass = document.getElementById('newPassword');
                const checks = {
                    length: document.getElementById('check-length'),
                    upper: document.getElementById('check-uppercase'),
                    lower: document.getElementById('check-lowercase'),
                    number: document.getElementById('check-number')
                };

                newPass.addEventListener('input', () => {
                    const val = newPass.value;

                    // Length
                    if (val.length >= 6) checks.length.className = 'valid';
                    else checks.length.className = 'invalid';

                    // Uppercase
                    if (/[A-Z]/.test(val)) checks.upper.className = 'valid';
                    else checks.upper.className = 'invalid';

                    // Lowercase
                    if (/[a-z]/.test(val)) checks.lower.className = 'valid';
                    else checks.lower.className = 'invalid';

                    // Number
                    if (/\d/.test(val)) checks.number.className = 'valid';
                    else checks.number.className = 'invalid';
                });

                // STEP 3: Reset Password
                document.getElementById('resetPasswordForm').addEventListener('submit', async (e) => {
                    e.preventDefault();
                    const pass = newPass.value;
                    const confirm = document.getElementById('confirmPassword').value;

                    if (pass !== confirm) return showMessage('error', 'Passwords do not match');

                    // Basic validation check
                    if (pass.length < 6 || !/[A-Z]/.test(pass) || !/[a-z]/.test(pass) || !/\d/.test(pass)) {
                        return showMessage('error', 'Password does not meet requirements');
                    }

                    const btn = document.getElementById('resetPasswordBtn');
                    btn.textContent = 'Resetting...';
                    btn.disabled = true;

                    try {
                        const res = await fetch('forgot-password', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: 'action=resetPassword&email=' + encodeURIComponent(userEmail) + '&newPassword=' + encodeURIComponent(pass)
                        });
                        const data = await res.json();

                        if (data.success) {
                            showMessage('success', 'Password reset successfully! Redirecting...');
                            setTimeout(() => window.location.href = '<c:url value="/auth.jsp?form=login&success=reset"/>', 2000);
                        } else {
                            showMessage('error', data.error || 'Reset failed');
                            btn.disabled = false;
                            btn.textContent = 'Reset Password';
                        }
                    } catch (err) {
                        showMessage('error', 'Network error');
                        btn.disabled = false;
                        btn.textContent = 'Reset Password';
                    }
                });
            </script>
        </body>

        </html>