package com.cloud.task.api;

import java.util.Collection;

import com.cloud.task.model.JobBriefInfo;

/**
 * 作业状态展示的API.
 *
 */
public interface IJobStatisticsService {

    /**
     * 获取作业总数.
     *
     * @return 作业总数.
     */
    int getJobsTotalCount();

    /**
     * 获取所有作业简明信息.
     *
     * @return 作业简明信息集合.
     */
    Collection<JobBriefInfo> getAllJobsBriefInfo();

    /**
     * 获取作业简明信息.
     *
     * @param jobName 作业名称
     * @return 作业简明信息.
     */
    JobBriefInfo getJobBriefInfo(String jobName);

    /**
     * 获取该IP下所有作业简明信息.
     *
     * @param ip 服务器IP
     * @return 作业简明信息集合.
     */
    Collection<JobBriefInfo> getJobsBriefInfo(String ip);
}
