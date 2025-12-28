(async function() {
    const user = JSON.parse(localStorage.getItem("user"));

    document.getElementById("navbar_name").innerText = user.fullname
    document.getElementById("navbar_name1").innerText = user.fullname
    document.getElementById("navbar_role").innerText = user.privilegeLevel;
})();