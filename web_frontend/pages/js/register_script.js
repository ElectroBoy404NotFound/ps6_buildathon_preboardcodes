const regtoastContent = document.getElementById("registerToastContent");

function redirectToLogin() {
    loadPagetoElementInstant("login", "contentArea", true, true);
}

function clearValidationStyles() {
    const fields = ["inputFirstName", "inputLastName", "inputEmail", "inputPassword", "inputlocation", "inputConfirmPassword"];
    fields.forEach(id => {
        document.getElementById(id).classList.remove("is-invalid");
    });
}

function showToast(message) {
    regtoastContent.innerHTML = "Registering...";
    new bootstrap.Toast(document.getElementById("myToast")).show();
    regtoastContent.innerHTML = message;
}

function hideToast() {
    document.getElementById("toastContainer").style.display = "none";
}

async function onRegister() {
    clearValidationStyles();

    const firstNameField = document.getElementById("inputFirstName");
    const lastNameField = document.getElementById("inputLastName");
    const emailField = document.getElementById("inputEmail");
    const passwordField = document.getElementById("inputPassword");
    const locationField = document.getElementById("inputlocation");
    const usernameField = document.getElementById("inputUsername");
    const passwordConfirmField = document.getElementById("inputConfirmPassword");

    const firstName = firstNameField.value.trim();
    const lastName = lastNameField.value.trim();
    const email = emailField.value.trim();
    const password = passwordField.value.trim();
    const location = locationField.value.trim();
    const username = usernameField.value.trim();
    const passwordConfirm = passwordConfirmField.value.trim();

    let errorMessages = [];

    if (firstName === "") {
        firstNameField.classList.add("is-invalid");
        errorMessages.push("First name is required.");
    }

    if (lastName === "") {
        lastNameField.classList.add("is-invalid");
        errorMessages.push("Last name is required.");
    }

    if (email === "") {
        emailField.classList.add("is-invalid");
        errorMessages.push("Email is required.");
    } else if (!/^\S+@\S+\.\S+$/.test(email)) {
        emailField.classList.add("is-invalid");
        errorMessages.push("Invalid email address.");
    }

    if (password === "") {
        passwordField.classList.add("is-invalid");
        errorMessages.push("Password is required.");
    } else if (password.length < 6) {
        passwordField.classList.add("is-invalid");
        errorMessages.push("Password must be at least 6 characters.");
    } else if (password !== passwordConfirm) {
        passwordField.classList.add("is-invalid");
        passwordConfirmField.classList.add("is-invalid");
        errorMessages.push("Passwords do not match.");
    }

    if(username === "") {
        usernameField.classList.add("is-invalid");
        errorMessages.push("Username is required.");
    } else if (username.length < 6) {
        usernameField.classList.add("is-invalid");
        errorMessages.push("Username must be at least 6 characters.");
    } else if (username.length > 20) {
        usernameField.classList.add("is-invalid");
        errorMessages.push("Username must be at most 20 characters.");
    }

    if (location === "") {
        locationField.classList.add("is-invalid");
        errorMessages.push("Location is required.");
    }

    if (errorMessages.length > 0) {
        showToast(errorMessages.join(" "));
        return false;
    }

    document.getElementById("reg_button").disabled = true;
    showToast("Registering...");

    const result = await createUser(email, password, firstName + " " + lastName, username, location);
    if("error" in result) {
        switch(result.error) {
            case 1002:
                showToast("Username/Email already in use!");
                break;
            default:
                showToast(`Unknown error ${result.error} has occured!`);
        }
        document.getElementById("reg_button").disabled = false;
        return;
    }

    showToast("Registered! Redirecting to the dashboard...");

    var login_response = await loginUser(email, password);

    const token = login_response["token"];
    const refreshtoken = login_response["refreshToken"];
    const userinfo = await getUserInfo(token);

    localStorage.setItem("usertoken", token);
    localStorage.setItem("userrefreshtoken", refreshtoken);
    localStorage.setItem("loggedin", true);
    localStorage.setItem("user", JSON.stringify(userinfo));

    window.location.reload();
}
