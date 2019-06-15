package com.cloud.task.model;

import lombok.Data;

/**
 * 〈作业执行事件查询body〉<br> 
 *
 * @author number68
 * @date 2019/5/14
 * @since 0.1
 */
@Data
public class JobExecutionQueryInfo {
    private String perPage;

    private String page;

    private String sort;

    private String order;

    private String startTime;

    private String endTime;

    private String jobName;

    private String source;

    private String executionType;

    private String state;

    private String ip;

    private String isSuccess;
}
