async function sidebar_onUploadFileClick() {
    loadPagetoElementInstant("uploadfile", "dashboard_content__", true, false, true);
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

async function dashboard__myfiles() {
    loadPagetoElementInstant("filelist", "dashboard_content__", true, false);
}

async function dashboardLoaded() {
    await refreshUserJWTToken();

    if(localStorage.getItem("redirect_to")) {
        const redirectTo = localStorage.getItem("redirect_to");
        localStorage.removeItem("redirect_to");
        loadPagetoElementInstant(redirectTo, "dashboard_content__", false, false);
        return;
    }
}

(async function() {
    await dashboardLoaded();
})();