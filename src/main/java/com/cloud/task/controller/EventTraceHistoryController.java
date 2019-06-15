package com.cloud.task.controller;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.task.model.JobExecutionQueryInfo;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbSearch;
import com.dangdang.ddframe.job.event.type.JobExecutionEvent;
import com.dangdang.ddframe.job.event.type.JobStatusTraceEvent;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 *〈EventTraceHistoryController〉<br>
 *
 * @author number68
 * @date 2019/4/28
 * @since 0.1
 */
@RestController
@RequestMapping("/api/event-trace")
@Api(description = "任务执行事件历史接口")
@Slf4j
public class EventTraceHistoryController {

    @Autowired
    private DataSource dataSource;

    /**
     * 查询作业执行事件.
     * 
     * @param queryParams 查询条件
     * @return 运行痕迹事件结果集
     * @throws ParseException 解析异常
     */
    @PostMapping(value = "/execution")
    public JobEventRdbSearch.Result<JobExecutionEvent> findJobExecutionEvents(
        @RequestBody(required = false) final JobExecutionQueryInfo queryParams) throws ParseException {
        if (null == queryParams) {
            return null;
        }

        JobEventRdbSearch jobEventRdbSearch = new JobEventRdbSearch(dataSource);
        return jobEventRdbSearch
            .findJobExecutionEvents(buildCondition(queryParams, new String[] {"jobName", "ip", "isSuccess"}));
    }

    /**
     * 查询作业状态事件.
     *
     * @param queryParams 查询条件
     * @return 运行痕迹事件结果集
     * @throws ParseException 解析异常
     */
    @PostMapping("/status")
    public JobEventRdbSearch.Result<JobStatusTraceEvent> findJobStatusTraceEvents(
        @RequestBody(required = false) final JobExecutionQueryInfo queryParams) throws ParseException {
        if (null == queryParams) {
            return null;
        }

        JobEventRdbSearch jobEventRdbSearch = new JobEventRdbSearch(dataSource);
        return jobEventRdbSearch.findJobStatusTraceEvents(
            buildCondition(queryParams, new String[] {"jobName", "source", "executionType", "state"}));
    }

    private JobEventRdbSearch.Condition buildCondition(final JobExecutionQueryInfo info, final String[] params)
        throws ParseException {
        int perPage = 10;
        int page = 1;
        if (!StringUtils.isEmpty(info.getPerPage())) {
            perPage = Integer.parseInt(info.getPerPage());
        }
        if (!StringUtils.isEmpty(info.getPage())) {
            page = Integer.parseInt(info.getPage());
        }

        String sort = info.getSort();
        String order = info.getOrder();
        Date startTime = null;
        Date endTime = null;
        Map<String, Object> fields = getQueryParameters(info, params);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!StringUtils.isEmpty(info.getStartTime())) {
            startTime = simpleDateFormat.parse(info.getStartTime());
        }
        if (!StringUtils.isEmpty(info.getEndTime())) {
            endTime = simpleDateFormat.parse(info.getEndTime());
        }
        return new JobEventRdbSearch.Condition(perPage, page, sort, order, startTime, endTime, fields);
    }

    private Map<String, Object> getQueryParameters(final JobExecutionQueryInfo info, final String[] params) {
        final Map<String, Object> result = new HashMap<>();
        for (String each : params) {
            try {
                Field field = JobExecutionQueryInfo.class.getDeclaredField(each);
                field.setAccessible(true);
                String value = (String)field.get(info);
                if (!StringUtils.isEmpty(value)) {
                    result.put(each, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.warn("JobExecutionQueryInfo has no field:{}", each);
                continue;
            }
        }
        return result;
    }
}
