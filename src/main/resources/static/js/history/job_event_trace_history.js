$(function () {
    $("[data-mask]").inputmask();
    $(".toolbar input").bind("keypress", function (event) {
        if ("13" == event.keyCode) {
            $("#job-exec-details-table").bootstrapTable("refresh", {silent: true});
        }
    });
    $("#job-exec-details-table").on("all.bs.table", function () {
        doLocale();
    });
});
