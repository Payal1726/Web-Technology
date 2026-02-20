const form = document.getElementById('registrationForm');
const username = document.getElementById('username');
const email = document.getElementById('email');
const password = document.getElementById('password');
const strengthBar = document.getElementById('strength-bar');
const togglePassword = document.getElementById('togglePassword');

// 1. STRICTURE: Prevent numbers/special chars as user types
username.addEventListener('input', (e) => {
    const originalValue = e.target.value;
    // Regex: Replace anything that is NOT a letter or space with empty string
    e.target.value = originalValue.replace(/[^A-Za-z\s]/g, '');
    
    if (originalValue !== e.target.value) {
        showError(username, "Numbers and special characters are not allowed!");
    } else {
        showSuccess(username);
    }
});

// 2. Password Toggle
togglePassword.addEventListener('click', () => {
    const type = password.type === 'password' ? 'text' : 'password';
    password.type = type;
    togglePassword.textContent = type === 'password' ? '👁️' : '🙈';
});

// 3. Password Strength logic
password.addEventListener('input', () => {
    let score = 0;
    const val = password.value;
    if (val.length >= 8) score += 25;
    if (/[A-Z]/.test(val)) score += 25;
    if (/[0-9]/.test(val)) score += 25;
    if (/[^A-Za-z0-9]/.test(val)) score += 25;

    strengthBar.style.width = score + '%';
    strengthBar.style.backgroundColor = score < 50 ? '#ef4444' : score < 100 ? '#f59e0b' : '#22c55e';
});

// 4. Validation Helpers
function showError(input, message) {
    const group = input.parentElement.closest('.input-group');
    group.className = 'input-group error';
    group.querySelector('.error-msg').innerText = message;
}

function showSuccess(input) {
    const group = input.parentElement.closest('.input-group');
    group.className = 'input-group success';
}

// 5. Final Submission Check
form.addEventListener('submit', (e) => {
    e.preventDefault();
    let isvalid = true;

    // Final Username Check
    if (username.value.trim() === "") {
        showError(username, "Name is required");
        isvalid = false;
    }

    // Email Check
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
        showError(email, "Invalid email format");
        isvalid = false;
    } else { showSuccess(email); }

    // Password Check
    if (password.value.length < 8) {
        showError(password, "Minimum 8 characters required");
        isvalid = false;
    }

    if (!isvalid) {
        document.getElementById('formContainer').style.animation = 'shake 0.4s ease';
        setTimeout(() => document.getElementById('formContainer').style.animation = '', 400);
    } else {
        alert("Success! Form is clean and strictly string-based.");
    }
});