package com.cloud.task.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloud.task.api.*;

/**
 * 作业API服务实现类.
 *
 */
@Service
public class IJobAPIServiceImpl implements IJobAPIService {
    @Autowired
    private IJobSettingsService jobSettingsService;
    @Autowired
    private IJobOperateService jobOperateService;
    @Autowired
    private IShardingOperateService shardingOperateService;
    @Autowired
    private IJobStatisticsService jobStatisticsService;
    @Autowired
    private IServerStatisticsService serverStatisticsService;
    @Autowired
    private IShardingStatisticsService shardingStatisticsService;

    @Override
    public IJobSettingsService getJobSettingsAPI() {
        return jobSettingsService;
    }

    @Override
    public IJobOperateService getJobOperatorAPI() {
        return jobOperateService;
    }

    @Override
    public IShardingOperateService getShardingOperateAPI() {
        return shardingOperateService;
    }

    @Override
    public IJobStatisticsService getJobStatisticsAPI() {
        return jobStatisticsService;
    }

    @Override
    public IServerStatisticsService getServerStatisticsAPI() {
        return serverStatisticsService;
    }

    @Override
    public IShardingStatisticsService getShardingStatisticsAPI() {
        return shardingStatisticsService;
    }

}
