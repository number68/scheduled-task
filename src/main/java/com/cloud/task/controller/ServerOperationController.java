package com.cloud.task.controller;

import java.util.Collection;

import com.cloud.task.api.IJobAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cloud.task.model.JobBriefInfo;
import com.cloud.task.model.ServerBriefInfo;
import com.google.common.base.Optional;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务器维度操作的RESTful API.
 *
 */
@RestController
@RequestMapping("/api/servers")
@Api(description = "服务器接口")
@Slf4j
public class ServerOperationController {

    @Autowired
    private IJobAPIService jobAPIService;

    /**
     * 获取服务器总数.
     * 
     * @return 服务器总数
     */
    @GetMapping("/count")
    public int getServersTotalCount() {
        return jobAPIService.getServerStatisticsAPI().getServersTotalCount();
    }

    /**
     * 获取服务器详情.
     * 
     * @return 服务器详情集合
     */
    @GetMapping
    public Collection<ServerBriefInfo> getAllServersBriefInfo() {
        return jobAPIService.getServerStatisticsAPI().getAllServersBriefInfo();
    }

    /**
     * 禁用作业.
     *
     * @param serverIp 服务器IP地址
     */
    @GetMapping("/{serverIp}/disable")
    public void disableServer(@PathVariable("serverIp") final String serverIp) {
        jobAPIService.getJobOperatorAPI().disable(Optional.<String>absent(), Optional.of(serverIp));
    }

    /**
     * 启用作业.
     *
     * @param serverIp 服务器IP地址
     */
    @GetMapping("/{serverIp}/enable")
    public void enableServer(@PathVariable("serverIp") final String serverIp) {
        jobAPIService.getJobOperatorAPI().enable(Optional.<String>absent(), Optional.of(serverIp));
    }

    /**
     * 终止作业.
     *
     * @param serverIp 服务器IP地址
     */
    @GetMapping("/{serverIp}/shutdown")
    public void shutdownServer(@PathVariable("serverIp") final String serverIp) {
        jobAPIService.getJobOperatorAPI().shutdown(Optional.<String>absent(), Optional.of(serverIp));
    }

    /**
     * 清理作业.
     *
     * @param serverIp 服务器IP地址
     */
    @DeleteMapping("/{serverIp}")
    public void removeServer(@PathVariable("serverIp") final String serverIp) {
        jobAPIService.getJobOperatorAPI().remove(Optional.<String>absent(), Optional.of(serverIp));
    }

    /**
     * 获取该服务器上注册的作业的简明信息.
     *
     * @param serverIp 服务器IP地址
     * @return 作业简明信息对象集合
     */
    @GetMapping("/{serverIp}/jobs")
    public Collection<JobBriefInfo> getJobs(@PathVariable("serverIp") final String serverIp) {
        return jobAPIService.getJobStatisticsAPI().getJobsBriefInfo(serverIp);
    }

    /**
     * 禁用作业.
     * 
     * @param serverIp 服务器IP地址
     * @param jobName 作业名称
     */
    @GetMapping("/{serverIp}/jobs/{jobName}/disable")
    public void disableServerJob(@PathVariable("serverIp") final String serverIp,
        @PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI().disable(Optional.of(jobName), Optional.of(serverIp));
    }

    /**
     * 启用作业.
     *
     * @param serverIp 服务器IP地址
     * @param jobName 作业名称
     */
    @GetMapping("/{serverIp}/jobs/{jobName}/enable")
    public void enableServerJob(@PathVariable("serverIp") final String serverIp,
        @PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI().enable(Optional.of(jobName), Optional.of(serverIp));
    }

    /**
     * 终止作业.
     *
     * @param serverIp 服务器IP地址
     * @param jobName 作业名称
     */
    @GetMapping("/{serverIp}/jobs/{jobName}/shutdown")
    public void shutdownServerJob(@PathVariable("serverIp") final String serverIp,
        @PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI().shutdown(Optional.of(jobName), Optional.of(serverIp));
    }

    /**
     * 清理作业.
     *
     * @param serverIp 服务器IP地址
     * @param jobName 作业名称
     */
    @DeleteMapping("/{serverIp}/jobs/{jobName}")
    public void removeServerJob(@PathVariable("serverIp") final String serverIp,
        @PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI().remove(Optional.of(jobName), Optional.of(serverIp));
    }
}
