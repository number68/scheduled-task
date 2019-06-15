package com.cloud.task.api;

/**
 * 操作分片的API.
 *
 */
public interface IShardingOperateService {

    /**
     * 禁用作业分片.
     * 
     * @param jobName 作业名称
     * @param item 分片项
     */
    void disable(String jobName, String item);

    /**
     * 启用作业分片.
     *
     * @param jobName 作业名称
     * @param item 分片项
     */
    void enable(String jobName, String item);
}
