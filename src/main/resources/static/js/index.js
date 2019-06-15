$(function () {
    $("#content").load("html/status/job/jobs_status_overview.html");
    $("#job-status").click(function () {
        $("#content").load("html/status/job/jobs_status_overview.html");
    });
    $("#server-status").click(function () {
        $("#content").load("html/status/server/servers_status_overview.html");
    });
    $("#event-trace-history").click(function () {
        $("#content").load("html/history/job_event_trace_history.html");
    });
    $("#status-history").click(function () {
        $("#content").load("html/history/job_status_history.html");
    });
    $("#help").click(function () {
        $("#content").load("html/help/help.html", null, function () {
            doLocale();
        });
    });
    switchLanguage();

    //初始化显示语言
    initLanguage();
});


