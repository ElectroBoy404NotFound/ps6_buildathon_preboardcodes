async function downloadFile(name, hash) {
    const res = await fetch(APIENDPOINT_DOWNLOADFILES + hash, {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${localStorage.getItem("usertoken")}`
        }
    });
    const blob = await res.blob();
    const url = URL.createObjectURL(blob);

    const a = document.createElement("a");
    a.href = url;
    a.download = name + ".mp4";
    document.body.appendChild(a);
    a.click();

    URL.revokeObjectURL(url);
    a.remove();
}

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

    for(var i in files_data) {
        const file = files_data[i];
        dataTable.row.add([
            file.id,
            file.filename,
            file.transcribed ? "Done" : "In progress",
            file.createdAt,
            file.transcribed ?
            `<button onclick="downloadFile('${file.filename}', '${file.hash}')" title="Download" class="btn btn-sm btn-sm-circle btn-success m-2">
                <i class="fa fa-download"></i>
            </button>`:""
        ]).draw(false);
    }
})();