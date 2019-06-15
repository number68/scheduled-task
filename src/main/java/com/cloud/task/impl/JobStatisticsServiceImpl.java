package com.cloud.task.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloud.task.api.IJobStatisticsService;
import com.cloud.task.model.JobBriefInfo;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.internal.config.LiteJobConfigurationGsonFactory;
import com.dangdang.ddframe.job.lite.internal.storage.JobNodePath;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;

/**
 * 作业状态展示的实现类.
 *
 */
@Service
public final class JobStatisticsServiceImpl implements IJobStatisticsService {

    @Autowired
    private CoordinatorRegistryCenter regCenter;

    @Override
    public int getJobsTotalCount() {
        return regCenter.getChildrenKeys("/").size();
    }

    @Override
    public Collection<JobBriefInfo> getAllJobsBriefInfo() {
        List<String> jobNames = regCenter.getChildrenKeys("/");
        List<JobBriefInfo> result = new ArrayList<>(jobNames.size());
        for (String each : jobNames) {
            JobBriefInfo jobBriefInfo = getJobBriefInfo(each);
            if (null != jobBriefInfo) {
                result.add(jobBriefInfo);
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public JobBriefInfo getJobBriefInfo(final String jobName) {
        JobNodePath jobNodePath = new JobNodePath(jobName);
        JobBriefInfo result = new JobBriefInfo();
        result.setJobName(jobName);
        String liteJobConfigJson = regCenter.get(jobNodePath.getConfigNodePath());
        if (null == liteJobConfigJson) {
            return null;
        }
        LiteJobConfiguration liteJobConfig = LiteJobConfigurationGsonFactory.fromJson(liteJobConfigJson);
        result.setDescription(liteJobConfig.getTypeConfig().getCoreConfig().getDescription());
        result.setCron(liteJobConfig.getTypeConfig().getCoreConfig().getCron());
        result.setInstanceCount(getJobInstanceCount(jobName));
        result.setShardingTotalCount(liteJobConfig.getTypeConfig().getCoreConfig().getShardingTotalCount());
        result.setStatus(getJobStatus(jobName));
        return result;
    }

    private JobBriefInfo.JobStatus getJobStatus(final String jobName) {
        JobNodePath jobNodePath = new JobNodePath(jobName);
        List<String> instances = regCenter.getChildrenKeys(jobNodePath.getInstancesNodePath());
        if (instances.isEmpty()) {
            return JobBriefInfo.JobStatus.CRASHED;
        }
        if (isAllDisabled(jobNodePath)) {
            return JobBriefInfo.JobStatus.DISABLED;
        }
        if (isHasShardingFlag(jobNodePath, instances)) {
            return JobBriefInfo.JobStatus.SHARDING_FLAG;
        }
        return JobBriefInfo.JobStatus.OK;
    }

    private boolean isAllDisabled(final JobNodePath jobNodePath) {
        List<String> serversPath = regCenter.getChildrenKeys(jobNodePath.getServerNodePath());
        int disabledServerCount = 0;
        for (String each : serversPath) {
            if (JobBriefInfo.JobStatus.DISABLED.name().equals(regCenter.get(jobNodePath.getServerNodePath(each)))) {
                disabledServerCount++;
            }
        }
        return disabledServerCount == serversPath.size();
    }

    private boolean isHasShardingFlag(final JobNodePath jobNodePath, final List<String> instances) {
        Set<String> shardingInstances = new HashSet<>();
        for (String each : regCenter.getChildrenKeys(jobNodePath.getShardingNodePath())) {
            String instanceId = regCenter.get(jobNodePath.getShardingNodePath(each, "instance"));
            if (null != instanceId && !instanceId.isEmpty()) {
                shardingInstances.add(instanceId);
            }
        }
        return !instances.containsAll(shardingInstances) || shardingInstances.isEmpty();
    }

    private int getJobInstanceCount(final String jobName) {
        return regCenter.getChildrenKeys(new JobNodePath(jobName).getInstancesNodePath()).size();
    }

    @Override
    public Collection<JobBriefInfo> getJobsBriefInfo(final String ip) {
        List<String> jobNames = regCenter.getChildrenKeys("/");
        List<JobBriefInfo> result = new ArrayList<>(jobNames.size());
        for (String each : jobNames) {
            JobBriefInfo jobBriefInfo = getJobBriefInfoByJobNameAndIp(each, ip);
            if (null != jobBriefInfo) {
                result.add(jobBriefInfo);
            }
        }
        Collections.sort(result);
        return result;
    }

    private JobBriefInfo getJobBriefInfoByJobNameAndIp(final String jobName, final String ip) {
        if (!regCenter.isExisted(new JobNodePath(jobName).getServerNodePath(ip))) {
            return null;
        }
        JobBriefInfo result = new JobBriefInfo();
        result.setJobName(jobName);
        result.setStatus(getJobStatusByJobNameAndIp(jobName, ip));
        result.setInstanceCount(getJobInstanceCountByJobNameAndIp(jobName, ip));
        return result;
    }

    private JobBriefInfo.JobStatus getJobStatusByJobNameAndIp(final String jobName, final String ip) {
        JobNodePath jobNodePath = new JobNodePath(jobName);
        String status = regCenter.get(jobNodePath.getServerNodePath(ip));
        if ("DISABLED".equalsIgnoreCase(status)) {
            return JobBriefInfo.JobStatus.DISABLED;
        } else {
            return JobBriefInfo.JobStatus.OK;
        }
    }

    private int getJobInstanceCountByJobNameAndIp(final String jobName, final String ip) {
        int instanceCount = 0;
        JobNodePath jobNodePath = new JobNodePath(jobName);
        List<String> instances = regCenter.getChildrenKeys(jobNodePath.getInstancesNodePath());
        for (String each : instances) {
            if (ip.equals(each.split("@-@")[0])) {
                instanceCount++;
            }
        }
        return instanceCount;
    }
}
