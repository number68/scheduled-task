package com.cloud.task.api;

import java.util.Collection;

import com.cloud.task.model.ServerBriefInfo;

/**
 * 作业服务器状态展示的API.
 *
 */
public interface IServerStatisticsService {

    /**
     * 获取作业服务器总数.
     *
     * @return 作业服务器总数
     */
    int getServersTotalCount();

    /**
     * 获取所有作业服务器简明信息.
     *
     * @return 作业服务器简明信息集合
     */
    Collection<ServerBriefInfo> getAllServersBriefInfo();
}
