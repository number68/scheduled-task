$(function () {
    $(".toolbar input").bind("keypress", function (event) {
        if ("13" == event.keyCode) {
            $("#job-exec-status-table").bootstrapTable("refresh", {silent: true});
        }
    });
    $("#job-exec-status-table").on("all.bs.table", function () {
        doLocale();
    });
});