package com.cloud.task.api;

import java.util.Collection;

import com.cloud.task.model.ShardingInfo;

/**
 * 作业分片状态展示的API.
 *
 */
public interface IShardingStatisticsService {

    /**
     * 获取作业分片信息集合.
     *
     * @param jobName 作业名称
     * @return 作业分片信息集合
     */
    Collection<ShardingInfo> getShardingInfo(String jobName);
}
