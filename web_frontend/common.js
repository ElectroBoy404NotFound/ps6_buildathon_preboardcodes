const protocol = window.location.protocol;
const hostname = window.location.hostname;

// For development
const serverport = 8087;

// For production
// const serverport = window.location.port || (protocol === "https:" ? 443 : 80);

// Auth Server
const APIENDPOINT_LOGIN = `${protocol}//${hostname}:${serverport}/userauth/login`;
const APIENDPOINT_USERCREATE = `${protocol}//${hostname}:${serverport}/userauth/register`;
const APIENDPOINT_REFRESH = `${protocol}//${hostname}:${serverport}/userauth/refresh`;
const APIENDPOINT_LOGOUT = `${protocol}//${hostname}:${serverport}/userauth/logout`;
const APIENDPOINT_TOKENINFO = `${protocol}//${hostname}:${serverport}/userauth/jwtInfo`;
const APIENDPOINT_RESETPASSWORDREQUEST = `${protocol}//${hostname}:${serverport}/userauth/resetPassword?email=`;
const APIENDPOINT_RESETPASSWORD = `${protocol}//${hostname}:${serverport}/userauth/resetPassword`;
const APIENDPOINT_PRIVILAGELEVEL = `${protocol}//${hostname}:${serverport}/info/users/me/privilage_level`;
const APIENDPOINT_SELFUSER = `${protocol}//${hostname}:${serverport}/info/users/me`;

const APIENDPOINT_LISTFILES = `${protocol}//${hostname}:${serverport}/files/listFiles`;

const APIENDPOINT_USERINFOBYID = `${protocol}//${hostname}:${serverport}/info/users/getById/`;
const APIENDPOINT_USERINFOUPDATE = `${protocol}//${hostname}:${serverport}/update/user/update`;
const APIENDPOINT_UNAPPROVEDUSERS = `${protocol}//${hostname}:${serverport}/admin/users/getUnapprovedUsers`;
const APIENDPOINT_ADMINAPPROVEUSER = `${protocol}//${hostname}:${serverport}/admin/users/approveUser/`;
const APIENDPOINT_ADMINREJECTUSER = `${protocol}//${hostname}:${serverport}/admin/users/rejectUser/`;
const APIENDPOINT_ADMINEDITUSER = `${protocol}//${hostname}:${serverport}/admin/users/update`;
const APIENDPOINT_USERSLIST = `${protocol}//${hostname}:${serverport}/admin/users/list`;
const APIENDPOINT_DELETEUSER = `${protocol}//${hostname}:${serverport}/admin/users/delete/`;
const APIENDPOINT_VIDEOPLAYBACK = `${protocol}//${hostname}:${serverport}/files/video/`;
const APIENDPOINT_GETFILE = `${protocol}//${hostname}:${serverport}/files/getFile/`;
const APIENDPOINT_DELETEFILE = `${protocol}//${hostname}:${serverport}/admin/files/delete/`;
const APIENDPOINT_UPLOADFILE = `${protocol}//${hostname}:${serverport}/admin/files/upload`;

const privilage_level_dict = {
    NONE: 0,
    GUEST: 1,
    ADMIN: 2,
    CMSADMIN: 3
};

const rev_privilage_level_dict = {
    0: "NONE",
    1: "GUEST",
    2: "ADMIN",
    3: "CMSADMIN"
};

const file_type_dict = {
    NONE: 0,
    IMAGE: 1,
    PDF: 2,
    VIDEO: 3
};

const rev_file_type_dict = {
    0: "NONE",
    1: "IMAGE",
    2: "PDF",
    3: "VIDEO"
};

let loadedPages = [];

function loadPagetoElementInstant(page, element, loadcss, loadjs, forceload, afterload) {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.forEach((tooltipTriggerEl) => {
        const tooltipInstance = bootstrap.Tooltip.getInstance(tooltipTriggerEl);
        if (tooltipInstance) {
            tooltipInstance.dispose();
        }
    });
    if(!loadedPages.includes(page) || forceload) {
        fetch(`pages/html/${page}.html`) // Fetch HTML file from server
            .then(response => response.text())
            .then(html => {
                document.getElementById(element).innerHTML = html;

                if(!loadjs) {
                    let script = document.createElement("script");
                    script.src = `pages/js/${page}_script.js`;
                    script.async = true;
                    document.body.appendChild(script);
                }
                if(!loadcss) {
                    let link = document.createElement("link");
                    link.rel = "stylesheet";
                    link.href = `pages/css/${page}_style.css`;
                    link.onload = () => console.log("Stylesheet loaded successfully");
                    link.onerror = () => console.error("Failed to load stylesheet");
                    document.head.appendChild(link);
                }

                if(afterload) afterload();
            });
        loadedPages.push(page);
    } else {
        fetch(`pages/html/${page}.html`) // Fetch HTML file from server
            .then(response => response.text())
            .then(html => {
                document.getElementById(element).innerHTML = html;
            });
    }
}

function loadComponent(component, loadcss, loadjs) {
    fetch(`components/html/component_${component}.html`) // Fetch HTML file from server
        .then(response => response.text())
        .then(html => {
            // document.getElementById("contentArea").insertAdjacentHTML("afterbegin", html);
            document.getElementById("contentArea").innerHTML += html;

            if(!loadjs) {
                let script = document.createElement("script");
                script.src = `components/js/component_${component}_script.js`;
                script.async = true;
                document.body.appendChild(script);
            }
            if(!loadcss) {
                let link = document.createElement("link");
                link.rel = "stylesheet";
                link.href = `components/css/component_${component}_style.css`;
                link.onload = () => console.log("Stylesheet loaded successfully");
                link.onerror = () => console.error("Failed to load stylesheet");
                document.head.appendChild(link);
            }
        });
}

function loadPagetoElement(page, element, loadcss, loadjs) {
    setTimeout(function () {
        loadPagetoElementInstant(page, element, loadcss, loadjs);
    }, 450);
}

async function loadPageIntoElementAfterComponents(page, components, element, loadcss, loadjs) {
    for (const component of components) {
        await new Promise((resolve) => {
            fetch(`components/html/component_${component.name}.html`)
                .then(response => response.text())
                .then(html => {
                    document.getElementById("contentArea").innerHTML += html;

                    if (!component.loadJS) {
                        const script = document.createElement("script");
                        script.src = `components/js/component_${component.name}_script.js`;
                        script.async = true;
                        document.body.appendChild(script);
                    }
                    if (!component.loadCSS) {
                        const link = document.createElement("link");
                        link.rel = "stylesheet";
                        link.href = `components/css/component_${component.name}_style.css`;
                        link.onload = () => console.log("Stylesheet loaded successfully");
                        link.onerror = () => console.error("Failed to load stylesheet");
                        document.head.appendChild(link);
                    }

                    setTimeout(resolve, 10);
                });
        });
    }

    loadPagetoElementInstant(page, element, loadcss, loadjs);
}

async function onPageLoad() {
    if(!localStorage.getItem("loggedin")) {
        loadPagetoElementInstant("login", "contentArea", false, false);
    } else {
        loadPageIntoElementAfterComponents("dashboard", [
            { name: "navbar", loadCSS: true, loadJS: false },
            { name: "sidebar", loadCSS: false, loadJS: false }
        ], "layoutSidenav_content", true, false);
    }
}

async function sendPOSTRequest(url, body, authtoken) {
    if(authtoken) {
        let response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${authtoken}`
            },
            body: JSON.stringify(body)
        });
        let result = await response.json();
        return result;
    }

    let response = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(body)
    });
    let result = await response.json();
    return result;
}

async function sendPOSTRequestForm(url, body, authtoken) {
    let response = await fetch(url, {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${authtoken}`
        },
        body: body
    });
    let result = await response.json();
    return result;
}

async function sendPOSTRequestString(url, body, authtoken) {
    let response = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "text/plain",
            "Authorization": `Bearer ${authtoken}`
        },
        body: body
    });
    let result = await response.json();
    return result;
}

async function sendGETRequest(url, authtoken) {
    if(authtoken) {
        let response = await fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${authtoken}`
            }
        });
        let result = await response.json();
        return result;
    }

    let response = await fetch(url, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        }
    });
    let result = await response.json();
    return result;
}

async function sendGETRequestBlob(url, authtoken) {
    let response = await fetch(url, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${authtoken}`
        }
    });
    let result = await response.blob();
    return result;
}

async function sendDELETERequest(url, reason, authtoken) {
    let response = await fetch(url, {
        method: "DELETE",
        headers: {
            "Content-Type": "plain/text",
            "Authorization": `Bearer ${authtoken}`
        },
        body: reason
    });
    let result = await response.json();
    return result;
}

function isEmptyOrWhitespace(str) {
    return str.trim().length === 0;
}

var newModal;

function createAndShowModal(header, body, footer) {
    // Create the modal HTML dynamically
    const modalHtml = `
        <div class="modal fade" id="newModal" tabindex="-1" aria-labelledby="newModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    ${header ? `
                        <!-- Modal Header -->
                        <div class="modal-header">
                            ${header}
                        </div>
                    ` : ""}
                    ${body ? `
                        <!-- Modal Body -->
                        <div class="modal-body">
                            ${body}
                        </div>
                    ` : ""}
                    ${footer ? `
                        <!-- Modal Footer -->
                        <div class="modal-footer">
                            ${footer}
                        </div>
                    ` : ""}
                </div>
            </div>
        </div>
    `;

    document.getElementById("modalContainer").innerHTML = modalHtml;

    newModal = new bootstrap.Modal(document.getElementById('newModal'));
    newModal.show();

    document.getElementById('newModal').addEventListener('hidden.bs.modal', function () {
        document.getElementById('newModal').remove();
    });
}

function closeModal() {
    newModal.hide();
}

function showTimedAlert(message, timeout = 5000, color="info") {
  const alertId = `alert-${Date.now()}`;
  const alertHTML = `
    <div id="${alertId}" class="alert alert-${color} alert-dismissible fade show mb-2 position-relative" role="alert" style="min-width: 300px;">
      ${message}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
      <div class="progress position-absolute bottom-0 start-0 w-100" style="height: 4px;">
        <div class="progress-bar bg-${color}" role="progressbar" style="width: 100%; transition: width ${timeout}ms linear;"></div>
      </div>
    </div>
  `;

  document.getElementById("alert-container").insertAdjacentHTML("beforeend", alertHTML);

  setTimeout(() => {
    const alertElement = document.getElementById(alertId);
    if (alertElement) {
      alertElement.classList.remove("show");
      alertElement.addEventListener("transitionend", () => {
        alertElement.remove();
      });
    }
  }, timeout);
}

function isotimeToHumanTime(inputstring) {
    // Remove the IANA zone part (e.g., [Asia/Kolkata])
    const cleaned = inputstring.replace(/\[.*?\]$/, "");
    const date = new Date(cleaned);

    return date.toLocaleString("en-US", {
        year: "numeric",
        month: "long",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
        hourCycle: "h23",
        timeZoneName: 'short'
    });
}

function timeSort(a, b) {
    const [h1, m1] = a.split(':').map(Number);
    const [h2, m2] = b.split(':').map(Number);
    return h1 !== h2 ? h1 - h2 : m1 - m2;
}
function validateEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(String(email).toLowerCase());
}

async function getUserInfoById(id, authtoken) {
    const url = APIENDPOINT_USERINFOBYID + id;
    try {
        const response = await sendGETRequest(url, authtoken);
        return response;
    } catch (error) {
        console.error("Error fetching user info by ID:", error);
        throw error;
    }
}
async function getUserList(authtoken) {
    const url = APIENDPOINT_USERSLIST;
    try {
        const response = await sendGETRequest(url, authtoken);
        return response;
    } catch (error) {
        console.error("Error fetching user list:", error);
        throw error;
    }
}
async function getPrivilageLevel(authtoken) {
    const url = APIENDPOINT_PRIVILAGELEVEL;
    try {
        const response = await sendGETRequest(url, authtoken);
        return response;
    } catch (error) {
        console.error("Error fetching privilage level:", error);
        throw error;
    }
}
async function loginUser(email, password) {
    const url = APIENDPOINT_LOGIN;
    const body = {
        email: email,
        password: password
    };
    try {
        const response = await sendPOSTRequest(url, body);
        return response;
    } catch (error) {
        console.error("Error logging in user:", error);
        throw error;
    }
}
async function getUserInfo(authtoken) {
    const url = APIENDPOINT_SELFUSER;
    try {
        const response = await sendGETRequest(url, authtoken);
        return response;
    } catch (error) {
        console.error("Error fetching user info:", error);
        throw error;
    }
}
async function refreshToken(refreshtoken) {
    try {
        const response = await sendPOSTRequest(APIENDPOINT_REFRESH, {
            refreshToken: refreshtoken
        });
        return response;
    } catch (error) {
        console.error("Error refreshing token:", error);
        throw error;
    }
}
async function getTokenInfo(authtoken) {
    const url = APIENDPOINT_TOKENINFO;
    try {
        const response = await sendPOSTRequest(url, {
            token: authtoken
        });
        return response;
    } catch (error) {
        console.error("Error fetching token info:", error);
        throw error;
    }
}
async function createUser(email, password, name, username, location, authtoken) {
    const url = APIENDPOINT_USERCREATE;
    const body = {
        email: email,
        password: password,
        fullname: name,
        username: username,
        location: location
    };
    try {
        const response = await sendPOSTRequest(url, body, authtoken);
        return response;
    } catch (error) {
        console.error("Error creating user:", error);
        throw error;
    }
}
async function getUnapprovedUsers(authtoken) {
    const url = APIENDPOINT_UNAPPROVEDUSERS;
    try {
        const response = await sendGETRequest(url, authtoken);
        return response;
    } catch (error) {
        console.error("Error fetching users:", error);
        throw error;
    }
}
async function approveUser(id, authtoken) {
    const url = APIENDPOINT_ADMINAPPROVEUSER + id;
    try {
        const response = await sendGETRequest(url, authtoken);
        return response;
    } catch (error) {
        console.error("Error approving user:", error);
        throw error;
    }
}
async function rejectUser(id, reason, authtoken) {
    const url = APIENDPOINT_ADMINREJECTUSER + id;
    try {
        const response = await sendPOSTRequestString(url, reason, authtoken);
        return response;
    } catch (error) {
        console.error("Error rejecting user:", error);
        throw error;
    }
}
async function deleteUserAdmin(id, reason) {
    const url = APIENDPOINT_DELETEUSER + id;
    try {
        const response = await sendDELETERequest(url, reason, localStorage.getItem("usertoken"));
        return response;
    } catch (error) {
        console.error("Error deleting user:", error);
        throw error;
    }
}
async function logoutUser() {
    const url = APIENDPOINT_LOGOUT;
    try {
        const response = await sendPOSTRequest(url, {
            refreshToken: localStorage.getItem("userrefreshtoken"),
            jwtToken: localStorage.getItem("usertoken")
        }, localStorage.getItem("usertoken"));
        return response;
    } catch (error) {
        console.error("Error logging out user:", error);
        throw error;
    }
}
async function updateUser(userData, authtoken) {
    const url = APIENDPOINT_ADMINEDITUSER;
    try {
        const response = await sendPOSTRequest(url, userData, authtoken);
        return response;
    } catch (error) {
        console.error("Error updating user:", error);
        throw error;
    }
}
async function resetPasswordRequest(email) {
    const url = APIENDPOINT_RESETPASSWORDREQUEST + email;
    try {
        const response = await sendGETRequest(url);
        return response;
    } catch (error) {
        console.error("Error requesting password reset:", error);
        throw error;
    }
}
async function resetPassword(otp, email, newPassword) {
    const url = APIENDPOINT_RESETPASSWORD;
    const body = {
        otp: otp,
        email: email,
        newpassword: newPassword
    };
    try {
        const response = await sendPOSTRequest(url, body);
        return response;
    } catch (error) {
        console.error("Error resetting password:", error);
        throw error;
    }
}
async function listFilesRequest(authtoken) {
    const url = APIENDPOINT_LISTFILES;
    try {
        const response = await sendGETRequest(url, authtoken);
        return response;
    } catch (error) {
        console.error("Error requesting file list:", error);
        throw error;
    }
}
async function getFileRequest(fileId, authtoken) {
    const url = APIENDPOINT_GETFILE + fileId;
    try {
        const response = await sendGETRequestBlob(url, authtoken);
        return response;
    } catch (error) {
        console.error("Error fetching file:", error);
        throw error;
    }
}
async function deleteFileRequest(fileId, authtoken) {
    const url = APIENDPOINT_DELETEFILE + fileId;
    const body = { };
    try {
        const response = await sendPOSTRequest(url, body, authtoken);
        return response;
    } catch (error) {
        console.error("Error resetting password:", error);
        throw error;
    }
}
async function uploadFileRequest(body, authtoken) {
    const url = APIENDPOINT_UPLOADFILE;
    try {
        const response = await sendPOSTRequestForm(url, body, authtoken);
        return response;
    } catch (error) {
        console.error("Error uploading file:", error);
        throw error;
    }
}

(async function () {
    await onPageLoad();
})();
