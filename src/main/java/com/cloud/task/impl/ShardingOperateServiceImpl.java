package com.cloud.task.impl;

import com.cloud.task.api.IShardingOperateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.lite.internal.storage.JobNodePath;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;

/**
 * 操作分片的实现类.
 *
 */
@Service
public final class ShardingOperateServiceImpl implements IShardingOperateService {

    @Autowired
    private CoordinatorRegistryCenter regCenter;

    @Override
    public void disable(final String jobName, final String item) {
        disableOrEnableJobs(jobName, item, true);
    }

    @Override
    public void enable(final String jobName, final String item) {
        disableOrEnableJobs(jobName, item, false);
    }

    private void disableOrEnableJobs(final String jobName, final String item, final boolean disabled) {
        JobNodePath jobNodePath = new JobNodePath(jobName);
        String shardingDisabledNodePath = jobNodePath.getShardingNodePath(item, "disabled");
        if (disabled) {
            regCenter.persist(shardingDisabledNodePath, "");
        } else {
            regCenter.remove(shardingDisabledNodePath);
        }
    }
}
