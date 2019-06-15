package com.cloud.task.api;

import com.google.common.base.Optional;

/**
 * 操作作业的API.
 *
 */
public interface IJobOperateService {

    /**
     * 作业立刻执行.
     *
     * <p>作业在不与上次运行中作业冲突的情况下才会启动, 并在启动后自动清理此标记.</p>
     *
     * @param jobName 作业名称
     * @param serverIp 作业服务器IP地址
     */
    void trigger(Optional<String> jobName, Optional<String> serverIp);

    /**
     * 作业禁用.
     * 
     * <p>会重新分片.</p>
     *
     * @param jobName 作业名称
     * @param serverIp 作业服务器IP地址
     */
    void disable(Optional<String> jobName, Optional<String> serverIp);

    /**
     * 作业启用.
     *
     * @param jobName 作业名称
     * @param serverIp 作业服务器IP地址
     */
    void enable(Optional<String> jobName, Optional<String> serverIp);

    /**
     * 作业关闭.
     *
     * @param jobName 作业名称
     * @param serverIp 作业服务器IP地址
     */
    void shutdown(Optional<String> jobName, Optional<String> serverIp);

    /**
     * 作业删除.
     * 
     * @param jobName 作业名称
     * @param serverIp 作业服务器IP地址
     */
    void remove(Optional<String> jobName, Optional<String> serverIp);
}
