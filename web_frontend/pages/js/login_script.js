const toastContent = document.getElementById("loginToastContent");

async function onRegisterClick() {
    loadPagetoElementInstant("register", "contentArea", true, false);
}

async function onForgotPasswordClick() {
    loadPagetoElementInstant("forgotpassword", "contentArea", true, false);
}

async function onLoginClick() {
    const email = document.getElementById("inputEmail").value;
    const password = document.getElementById("inputPassword").value;

    toastContent.innerHTML = "Logging in...";
    new bootstrap.Toast(document.getElementById("myToast")).show();

    if(!validateEmail(email)) {
        toastContent.innerHTML = "Please enter a valid email address!";
    } else if(isEmptyOrWhitespace(password)) {
        toastContent.innerHTML = "Password cannot be empty!";
    } else if(password.length < 8) {
        toastContent.innerHTML = "Password should be atleast 8 characters long!";
    } else {
        var login_response = await loginUser(email, password);

        if("error" in login_response) {
            switch(login_response["error"]) {
                case 1003:
                    toastContent.innerHTML = "Incorrect email-password combo!";
                    break;
                case 1004:
                    toastContent.innerHTML = "This account is not yet accepted or is disabled!";
                    break;
                default:
                    toastContent.innerHTML = `Unknown error ${login_response["error"]}`;
            }
            new bootstrap.Toast(document.getElementById("myToast")).show();
            return;
        }

        const token = login_response["token"];
        const refreshtoken = login_response["refreshToken"];
        localStorage.setItem("usertoken", token);
        localStorage.setItem("userrefreshtoken", refreshtoken);
        localStorage.setItem("loggedin", true);
        const userinfo = await getUserInfo(token);
        localStorage.setItem("user", JSON.stringify(userinfo));

        toastContent.innerHTML = "Logged in, redirecting...";

        location.reload();
    }
    new bootstrap.Toast(document.getElementById("myToast")).show();
}