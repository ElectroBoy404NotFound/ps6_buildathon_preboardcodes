(async function() {
    document.getElementById('resetForm').addEventListener('submit', async function(event) {
        event.preventDefault();

        const otp = document.getElementById('otp').value.trim();
        const password = document.getElementById('newPassword').value.trim();
        const confirmPassword = document.getElementById('confirmPassword').value.trim();
        const email = localStorage.getItem("resetpassword_email");
        
        // 🔍 Validation checks
        if (!/^\d{6}$/.test(otp)) {
            showTimedAlert('OTP must be a 6-digit number.');
            return;
        }

        if (password.length < 6) {
            showTimedAlert('Password must be at least 6 characters long.');
            return;
        }

        if (password !== confirmPassword) {
            showTimedAlert('Passwords do not match!');
            return;
        }

        const response = await resetPassword(otp, email, password);
        if ("error" in response) {
            switch(response.error) {
                case 1011:
                    showTimedAlert('OTP is invalid!');
                    break;
                default:
                    showTimedAlert(`Error: ${response.error}`);
            }
            return;
        }

        loadPagetoElementInstant("login", "contentArea", true, false, false, () => {
            showTimedAlert('Password reset successful! You can now log in with your new password.', 5000);
        });
    });
})();