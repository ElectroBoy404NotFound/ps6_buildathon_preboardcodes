(async function () {
    const userData = JSON.parse(localStorage.getItem("edituser"));
    if (!userData) {
        showTimedAlert("No user selected for editing.");
        return;
    }

    // Prefill form
    document.getElementById("userUsername").value = userData.username;
    document.getElementById("userEmail").value = userData.email;
    document.getElementById("userRole").value = userData.privilageLevel;
    document.getElementById("userLocation").value = userData.location;

    document.getElementById("editUserForm").addEventListener("submit", async function (e) {
        e.preventDefault();

        const form = new FormData(this);
        const updatedUser = {
            userid: userData.id,
            username: form.get("username"),
            email: form.get("email"),
            privilageLevel: form.get("role"),
            location: form.get("location")
        };
        const newPassword = form.get("password");
        if (newPassword) updatedUser.password = newPassword;

        const response = await updateUser(updatedUser, localStorage.getItem("usertoken"));
        if ("error" in response) {
            switch(response.error) {
                case 1008:
                    showTimedAlert("Username/email already in use.");
                    break;
                default:
                    showTimedAlert(`Error updating user: ${response.error}`);
            }
            return;
        }

        loadPagetoElementInstant("dashboard", "dashboard_content__", true, true, true, () => {
            showTimedAlert("User updated successfully!");
        });
    });
})();
