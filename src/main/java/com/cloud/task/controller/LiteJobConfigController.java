package com.cloud.task.controller;

import com.cloud.task.api.IJobAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cloud.task.model.Job;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * 作业配置的RESTful API.
 *
 */
@RestController
@RequestMapping("/api/jobs/config")
@Api(description = "任务配置接口")
@Slf4j
public class LiteJobConfigController {
    @Autowired
    private IJobAPIService jobAPIService;

    /**
     * 获取作业配置.
     * 
     * @param jobName 作业名称
     * @return 作业配置
     */
    @GetMapping("/{jobName}")
    public Job getJobSettings(@PathVariable final String jobName) {
        return jobAPIService.getJobSettingsAPI().getJobSettings(jobName);
    }

    /**
     * 修改作业配置.
     * 
     * @param jobSettings 作业配置
     */
    @PostMapping("/update")
    public void updateJobSettings(@RequestBody final Job jobSettings) {
        jobAPIService.getJobSettingsAPI().updateJobSettings(jobSettings);
    }

    /**
     * 删除作业配置.
     * 
     * @param jobName 作业名称
     */
    @DeleteMapping("/{jobName}")
    public void removeJob(@PathVariable final String jobName) {
        jobAPIService.getJobSettingsAPI().removeJobSettings(jobName);
    }
}
