
$(document).ready(function () {

    $("#topNav").load("/fragments/top-nav.html");
    $("#sideNav").load("/fragments/side-nav.html");


    $.ajax({
        url: '/api/user/profile',
        method: 'GET',
        xhrFields: {
            withCredentials: true
        },
        success: function (loggedUser) {
            let roles = loggedUser.role;

            $("#loggedUserName").text(loggedUser.name);
            $("#loggedUserRoles").text(roles.join(', '));

            if (roles.includes("ADMIN")) {
                $("#admin_nav").show();
            } else {
                $("#admin_nav").hide();
            }

            if (roles.includes("USER")) {
                $("#user_nav").show();
            } else {
                $("#user_nav").hide();
            }
        },
        error: function () {
            alert("Не удалось загрузить данные о пользователе.");
        }
    });
});