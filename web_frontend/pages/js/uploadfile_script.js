(async function() {
    document.getElementById("addFileForm").addEventListener("submit", async function (e) {
        e.preventDefault();
        
        const inputformData = new FormData(this);

        document.querySelectorAll("#addFileForm input, #addFileForm button, #addFileForm select").forEach(el => {
            el.disabled = true;
        });

        const filename = inputformData.get("name");
        const fileFile = inputformData.get("images");

        const tosendformData = new FormData();
        tosendformData.append("filename", filename);
        tosendformData.append("file", fileFile);

        const response = await uploadFileRequest(tosendformData, localStorage.getItem("usertoken"));
        if ("error" in response) {
            console.error("Failed to upload file:", response.error);
            showTimedAlert("Failed to upload file, please try again later. Error: " + response.error, 3000);
            document.querySelectorAll("#addFileForm input, #addFileForm button, #addFileForm select").forEach(el => {
                el.disabled = false;
            });

            return;
        }

        loadPagetoElementInstant("dashboard", "dashboard_content__", true, true, true, () => {
            showTimedAlert(`File "${response.filename}" added successfully with id ${response.id}`, 3000);
        });
    });
})();