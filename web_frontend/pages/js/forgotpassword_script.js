async function onResetPasswordClick() {
    const email = document.getElementById("inputEmail").value;
    document.getElementById("resetpasswordbutton").disabled = true;

    if (!validateEmail(email)) {
        showTimedAlert("Please enter a valid email address!", 3000);
        document.getElementById("resetpasswordbutton").disabled = false;
        return;
    }
    const response = await resetPasswordRequest(email);
    if("error" in response) {
        switch(response.error) {
            case 1009:
                showTimedAlert("Email not found.", 3000);
                break;
            default:
                showTimedAlert(`Error: ${response.error}`, 3000);
        }
        document.getElementById("resetpasswordbutton").disabled = false;
        return;
    }

    localStorage.setItem("resetpassword_email", email);
    loadPagetoElementInstant("forgotpassword_entry", "contentArea", true, false);
}