package com.cloud.task.api;

import com.cloud.task.model.Job;

/**
 * 作业配置的API.
 */
public interface IJobSettingsService {

    /**
     * 获取作业设置.
     *
     * @param jobName 作业名称
     * @return 作业设置对象
     */
    Job getJobSettings(String jobName);

    /**
     * 更新作业设置.
     *
     * @param jobSettings 作业设置对象
     */
    void updateJobSettings(Job jobSettings);

    /**
     * 删除作业设置.
     *
     * @param jobName 作业名称
     */
    void removeJobSettings(final String jobName);
}
