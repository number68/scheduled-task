package com.cloud.task.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.cloud.task.api.IShardingStatisticsService;
import com.cloud.task.model.ShardingInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.lite.internal.storage.JobNodePath;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;

/**
 * 作业分片状态展示的实现类.
 *
 */
@Service
public final class ShardingStatisticsServiceImpl implements IShardingStatisticsService {

    @Autowired
    private CoordinatorRegistryCenter regCenter;

    @Override
    public Collection<ShardingInfo> getShardingInfo(final String jobName) {
        String shardingRootPath = new JobNodePath(jobName).getShardingNodePath();
        List<String> items = regCenter.getChildrenKeys(shardingRootPath);
        List<ShardingInfo> result = new ArrayList<>(items.size());
        for (String each : items) {
            result.add(getShardingInfo(jobName, each));
        }
        Collections.sort(result);
        return result;
    }

    private ShardingInfo getShardingInfo(final String jobName, final String item) {
        ShardingInfo result = new ShardingInfo();
        result.setItem(Integer.parseInt(item));
        JobNodePath jobNodePath = new JobNodePath(jobName);
        String instanceId = regCenter.get(jobNodePath.getShardingNodePath(item, "instance"));
        boolean disabled = regCenter.isExisted(jobNodePath.getShardingNodePath(item, "disabled"));
        boolean running = regCenter.isExisted(jobNodePath.getShardingNodePath(item, "running"));
        boolean shardingError = !regCenter.isExisted(jobNodePath.getInstanceNodePath(instanceId));
        result.setStatus(ShardingInfo.ShardingStatus.getShardingStatus(disabled, running, shardingError));
        result.setFailover(regCenter.isExisted(jobNodePath.getShardingNodePath(item, "failover")));
        if (null != instanceId) {
            String[] ipAndPid = instanceId.split("@-@");
            result.setServerIp(ipAndPid[0]);
            result.setInstanceId(ipAndPid[1]);
        }
        return result;
    }
}
