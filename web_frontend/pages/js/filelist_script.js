(async function() {
    const table = document.getElementById("files_list_table");
    const dataTable = new DataTable(table);

    const files_data = await listFilesRequest(localStorage.getItem("usertoken"));
    console.log("Files data:", files_data);
    if ("error" in files_data) {
        localStorage.setItem("redirect_to", "filelist");
        window.location.reload();
        return;
    }

    for(var i in files) {
        const file = files[i];
        dataTable.row.add([
            file.id,
            file.name,
            file.transcribed ? "Done" : "In progress",
            file.transcribed ?
            `<button onclick="editHallCMSLIST(${hall.id})" title="Download" class="btn btn-sm btn-sm-circle btn-success m-2">
                <i class="fa fa-down"></i>
            </button>`:""
        ]).draw(false);
    }
})();