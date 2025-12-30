async function actualDeleteUserAdmin(id) {
    const reason = document.getElementById("reject_reason").value.trim();
    const response = await deleteUserAdmin(id, reason, localStorage.getItem("usertoken"));
    if("error" in response) {
        await refreshUserJWTToken();
        actualDeleteUserAdmin(id);
        return;
    }

    showTimedAlert(`User ${id} deleted successfully!`, 3000);

    const table = document.getElementById("manageusers_table");
    const dataTable = new DataTable(table);

    loadUserListTable(dataTable);

    closeModal();
}
async function deleteUserAdminPanel(id) {
    createAndShowModal(
        "Please Enter Reason for Deletion",
        `<div class="mb-3">
            <label for="reject_reason" class="form-label">Reason for Deletion</label>
            <input type="text" class="form-control" id="reject_reason" placeholder="Enter reason for deletion">
        </div>`,
        `<button type='button' class='btn btn-secondary' data-bs-dismiss='modal'>Close</button> <button type='button' class='btn btn-danger' onclick='actualDeleteUserAdmin(${id})'>Delete</button>`,
    );
}
async function loadUserListTable(dataTable) {
    const users_data = await getUserList(localStorage.getItem("usertoken"));
    if ("error" in users_data) {
        localStorage.setItem("redirect_to", "adminmanageusers_users");
        window.location.reload();
        return;
    }

    localStorage.setItem("users_data", JSON.stringify(users_data));

    dataTable.clear().draw();

    for (const user of users_data) {
        const rowNode = dataTable.row.add([
            user.id,
            user.fullname,
            user.username,
            user.email,
            user.location,
            `${user.username == "guest" ? "Default Guest cannot be edited or deleted" :
            `${user.id == JSON.parse(localStorage.getItem("user")).id ? ``
                : `<button onclick="editUserAdmin(${user.id})" class="btn btn-sm btn-sm-circle btn-warning m-2" data-user-id="${user.id}">
                <i class="fa fa fa-key"></i>
            </button>
            <button onclick="deleteUserAdminPanel(${user.id})" class="btn btn-sm btn-sm-circle btn-danger m-2" data-user-id="${user.id}">
                <i class="fa fa fa-user-minus"></i>
            </button>`}` }`
        ]).draw(false).node();

        rowNode.setAttribute("data-user-id", user.id);
    }
}
async function editUserAdmin(id) {
    const users_data = JSON.parse(localStorage.getItem("users_data"));
    localStorage.removeItem("users_data")
    const user = users_data.find(u => u.id === id);
    localStorage.setItem("edituser", JSON.stringify(user));
    localStorage.setItem("edituser_id", id);
    loadPagetoElementInstant("adminedituser", "dashboard_content__", true, false, true);
}

(async function() {
    const table = document.getElementById("manageusers_table");
    const dataTable = new DataTable(table);

    loadUserListTable(dataTable);
})();