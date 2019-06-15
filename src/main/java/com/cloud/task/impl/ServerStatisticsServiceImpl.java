package com.cloud.task.impl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.cloud.task.api.IServerStatisticsService;
import com.cloud.task.model.ServerBriefInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.lite.internal.storage.JobNodePath;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;

/**
 * 作业服务器状态展示的实现类.
 *
 */
@Service
public final class ServerStatisticsServiceImpl implements IServerStatisticsService {

    @Autowired
    private CoordinatorRegistryCenter regCenter;

    @Override
    public int getServersTotalCount() {
        Set<String> servers = new HashSet<>();
        for (String jobName : regCenter.getChildrenKeys("/")) {
            JobNodePath jobNodePath = new JobNodePath(jobName);
            for (String each : regCenter.getChildrenKeys(jobNodePath.getServerNodePath())) {
                servers.add(each);
            }
        }
        return servers.size();
    }

    @Override
    public Collection<ServerBriefInfo> getAllServersBriefInfo() {
        ConcurrentHashMap<String, ServerBriefInfo> servers = new ConcurrentHashMap<>();
        for (String jobName : regCenter.getChildrenKeys("/")) {
            JobNodePath jobNodePath = new JobNodePath(jobName);
            for (String each : regCenter.getChildrenKeys(jobNodePath.getServerNodePath())) {
                servers.putIfAbsent(each, new ServerBriefInfo(each));
                ServerBriefInfo serverInfo = servers.get(each);
                if ("DISABLED".equalsIgnoreCase(regCenter.get(jobNodePath.getServerNodePath(each)))) {
                    serverInfo.getDisabledJobsNum().incrementAndGet();
                }
                serverInfo.getJobNames().add(jobName);
                serverInfo.setJobsNum(serverInfo.getJobNames().size());
            }
            List<String> instances = regCenter.getChildrenKeys(jobNodePath.getInstancesNodePath());
            for (String each : instances) {
                String serverIp = each.split("@-@")[0];
                ServerBriefInfo serverInfo = servers.get(serverIp);
                if (null != serverInfo) {
                    serverInfo.getInstances().add(each);
                    serverInfo.setInstancesNum(serverInfo.getInstances().size());
                }
            }
        }
        List<ServerBriefInfo> result = new ArrayList<>(servers.values());
        Collections.sort(result);
        return result;
    }
}
