async function sidebar_onUploadFileClick() {
    loadPagetoElementInstant("uploadfile", "dashboard_content__", true, false, true);
}
async function sidebar_onListFilesClick() {
    loadPagetoElementInstant("filelist", "dashboard_content__", true, false, true);
}

async function navbar_logoutUser() {
    const logoutResponse = await logoutUser(localStorage.getItem("usertoken"));
    if ("error" in logoutResponse) {
        console.error("Failed to logout:", logoutResponse.error);
        showTimedAlert("Failed to logout, please try again later.", 3000);
        return;
    }
    localStorage.clear();
    
    loadPagetoElementInstant("login", "contentArea", false, false);
}

async function refreshUserJWTToken() {
    const tokenInfo = await getTokenInfo(localStorage.getItem("usertoken"));

    if("error" in tokenInfo || tokenInfo.expired) {
        const refreshResponse = await refreshToken(localStorage.getItem("userrefreshtoken"));
        if("error" in refreshResponse) {
            console.error("Failed to refresh token:", refreshResponse.error);
            localStorage.removeItem("usertoken");
            localStorage.removeItem("userrefreshtoken");
            localStorage.removeItem("loggedin");
            window.location.reload();
            return;
        }

        localStorage.setItem("usertoken", refreshResponse.token);
    }
}

async function sidebar_adminonManageUsersListClick() {
    loadPagetoElementInstant("adminmanageusers_users", "dashboard_content__", true, false, true);
}

async function dashboardLoaded() {
    await refreshUserJWTToken();

    const user = JSON.parse(localStorage.getItem("user"));
    console.log(user);
    console.log(privilage_level_dict[user.privilegeLevel]);
    console.log(privilage_level_dict["ADMIN"]);
    if(privilage_level_dict[user.privilegeLevel] >= privilage_level_dict["ADMIN"]) {
        document.getElementById("admin_sidebar").removeAttribute("hidden");
    }

    if(localStorage.getItem("redirect_to")) {
        const redirectTo = localStorage.getItem("redirect_to");
        localStorage.removeItem("redirect_to");
        loadPagetoElementInstant(redirectTo, "dashboard_content__", false, false);
        return;
    } else
        loadPagetoElementInstant("dashboard_ui", "dashboard_content__", true, false);
}

(async function() {
    await dashboardLoaded();
})();