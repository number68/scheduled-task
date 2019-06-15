$(function () {
    renderSkin();
    controlSubMenuStyle();
});

function renderSidebarMenu(div) {
    div.parent().children().removeClass("active");
    div.parent().children().children().children("li").removeClass("active");
    div.parent().children().children("ul").css("display", "");
    div.addClass("active");
}

var my_skins = [
    "skin-blue",
    "skin-black",
    "skin-red",
    "skin-yellow",
    "skin-purple",
    "skin-green",
    "skin-blue-light",
    "skin-black-light",
    "skin-red-light",
    "skin-yellow-light",
    "skin-purple-light",
    "skin-green-light"
];

function renderSkin() {
    $("[data-skin]").on("click", function (event) {
        event.preventDefault();
        changeSkin($(this).data("skin"));
    });
}

function changeSkin(skinClass) {
    $.each(my_skins, function (index) {
        $("body").removeClass(my_skins[index]);
    });
    $("body").addClass(skinClass);
}

function controlSubMenuStyle() {
    $(".sub-menu").click(function () {
        $(this).parent().parent().children().removeClass("active");
        $(this).parent().addClass("active");
    });
}

function refreshJobNavTag() {
    $.ajax({
        url: "/api/jobs/count",
        cache: false,
        success: function (data) {
            $("#job-nav-tag").text(data);
        }
    });
}

function refreshServerNavTag() {
    $.ajax({
        url: "/api/servers/count",
        cache: false,
        success: function (data) {
            $("#server-nav-tag").text(data);
        }
    });
}
