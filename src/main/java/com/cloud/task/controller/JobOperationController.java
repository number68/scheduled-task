package com.cloud.task.controller;

import java.util.Collection;

import com.cloud.task.api.IJobAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cloud.task.model.JobBriefInfo;
import com.cloud.task.model.ShardingInfo;
import com.google.common.base.Optional;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * 作业维度操作的RESTful API.
 *
 */
@RestController
@RequestMapping("/api/jobs")
@Api(description = "作业接口")
@Slf4j
public class JobOperationController {

    @Autowired
    private IJobAPIService jobAPIService;

    /**
     * 获取作业总数.
     * 
     * @return 作业总数
     */
    @GetMapping("/count")
    public int getJobsTotalCount() {
        return jobAPIService.getJobStatisticsAPI().getJobsTotalCount();
    }

    /**
     * 获取作业详情.
     * 
     * @return 作业详情集合
     */
    @PostMapping
    public Collection<JobBriefInfo> getAllJobsBriefInfo() {
        return jobAPIService.getJobStatisticsAPI().getAllJobsBriefInfo();
    }

    /**
     * 触发作业.
     * 
     * @param jobName 作业名称
     */
    @GetMapping("/{jobName}/trigger")
    public void triggerJob(@PathVariable final String jobName) {
        jobAPIService.getJobOperatorAPI().trigger(Optional.of(jobName), Optional.<String>absent());
    }

    /**
     * 禁用作业.
     * 
     * @param jobName 作业名称
     */
    @GetMapping("/{jobName}/disable")
    public void disableJob(@PathVariable final String jobName) {
        jobAPIService.getJobOperatorAPI().disable(Optional.of(jobName), Optional.<String>absent());
    }

    /**
     * 启用作业.
     *
     * @param jobName 作业名称
     */
    @GetMapping("/{jobName}/enable")
    public void enableJob(@PathVariable final String jobName) {
        jobAPIService.getJobOperatorAPI().enable(Optional.of(jobName), Optional.<String>absent());
    }

    /**
     * 终止作业.
     * 
     * @param jobName 作业名称
     */
    @GetMapping("/{jobName}/shutdown")
    public void shutdownJob(@PathVariable final String jobName) {
        jobAPIService.getJobOperatorAPI().shutdown(Optional.of(jobName), Optional.<String>absent());
    }

    /**
     * 获取分片信息.
     * 
     * @param jobName 作业名称
     * @return 分片信息集合
     */
    @GetMapping("/{jobName}/sharding")
    public Collection<ShardingInfo> getShardingInfo(@PathVariable final String jobName) {
        return jobAPIService.getShardingStatisticsAPI().getShardingInfo(jobName);
    }

    @GetMapping("/{jobName}/sharding/{item}/disable")
    public void disableSharding(@PathVariable final String jobName, @PathVariable final String item) {
        jobAPIService.getShardingOperateAPI().disable(jobName, item);
    }

    @GetMapping("/{jobName}/sharding/{item}/enable")
    public void enableSharding(@PathVariable final String jobName, @PathVariable final String item) {
        jobAPIService.getShardingOperateAPI().enable(jobName, item);
    }
}
