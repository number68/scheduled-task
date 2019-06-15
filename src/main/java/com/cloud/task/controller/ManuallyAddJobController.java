package com.cloud.task.controller;

/**
 *〈手动执行task controller〉<br>
 *
 * @author number68
 * @date 2019/4/28
 * @since 0.1
 */

import com.cloud.task.model.JSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.task.constant.TaskConstants;
import com.cloud.task.handler.ScheduledTaskBuilder;
import com.cloud.task.model.Job;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/manuallyJob")
@Api(description = "手动执行任务接口")
@Slf4j
public class ManuallyAddJobController {
    @Autowired
    private ScheduledTaskBuilder scheduledTaskBuilder;

    /**
     * 添加动态任务（适用于脚本逻辑已存在的情况，只是动态添加了触发的时间）
     * @param job 任务信息
     * @return
     */
    @PostMapping("/addJob")
    public Object addJob(@RequestBody Job job) {
        JSONResult result = JSONResult.getSuccessResult();
        String errorMsg = checkJob(job);
        if (!StringUtils.isEmpty(errorMsg)) {
            result.setStatus("failure");
            result.setMsg(errorMsg);
            return result;
        }

        try {
            scheduledTaskBuilder.buildManualScheduledTaskAndInit(job, job.isRunOnce());
        } catch (Exception e) {
            result.setStatus("failure");
            result.setMsg(e.getMessage());
        }
        return result;
    }

    private String checkJob(Job job) {
        if (!StringUtils.hasText(job.getJobName())) {
            return "name must not be null";
        }

        if (!StringUtils.hasText(job.getCron())) {
            return "cron must not be null";
        }

        if (!StringUtils.hasText(job.getJobType())) {
            return "JobType must not be null";
        }

        if (TaskConstants.SCRIPT_JOB_TYPE.equals(job.getJobType())) {
            if (!StringUtils.hasText(job.getScriptCommandLine())) {
                return "ScriptCommandLine must not be null";
            }
        } else {
            if (!StringUtils.hasText(job.getJobClass())) {
                return "JobClass must not be null";
            }
        }
        return "";
    }
}
