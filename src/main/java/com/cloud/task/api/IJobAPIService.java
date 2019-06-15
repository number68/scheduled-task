package com.cloud.task.api;

public interface IJobAPIService {

    IJobSettingsService getJobSettingsAPI();

    IJobOperateService getJobOperatorAPI();

    IShardingOperateService getShardingOperateAPI();

    IJobStatisticsService getJobStatisticsAPI();

    IServerStatisticsService getServerStatisticsAPI();

    IShardingStatisticsService getShardingStatisticsAPI();

}
