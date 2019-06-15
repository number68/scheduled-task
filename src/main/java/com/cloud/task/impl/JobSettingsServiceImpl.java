package com.cloud.task.impl;

import com.cloud.task.api.IJobSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloud.task.model.Job;
import com.dangdang.ddframe.job.api.JobType;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.internal.config.LiteJobConfigurationGsonFactory;
import com.dangdang.ddframe.job.lite.internal.storage.JobNodePath;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * 作业配置的实现类.
 *
 */
@Service
public final class JobSettingsServiceImpl implements IJobSettingsService {

    @Autowired
    private CoordinatorRegistryCenter regCenter;

    @Override
    public Job getJobSettings(final String jobName) {
        Job result = new Job();
        JobNodePath jobNodePath = new JobNodePath(jobName);
        LiteJobConfiguration liteJobConfig =
            LiteJobConfigurationGsonFactory.fromJson(regCenter.get(jobNodePath.getConfigNodePath()));
        String jobType = liteJobConfig.getTypeConfig().getJobType().name();
        buildSimpleJobSettings(jobName, result, liteJobConfig);
        if (JobType.DATAFLOW.name().equals(jobType)) {
            buildDataflowJobSettings(result, (DataflowJobConfiguration)liteJobConfig.getTypeConfig());
        }
        if (JobType.SCRIPT.name().equals(jobType)) {
            buildScriptJobSettings(result, (ScriptJobConfiguration)liteJobConfig.getTypeConfig());
        }
        return result;
    }

    private void buildSimpleJobSettings(final String jobName, final Job result,
        final LiteJobConfiguration liteJobConfig) {
        result.setJobName(jobName);
        result.setJobType(liteJobConfig.getTypeConfig().getJobType().name());
        result.setJobClass(liteJobConfig.getTypeConfig().getJobClass());
        result.setShardingTotalCount(liteJobConfig.getTypeConfig().getCoreConfig().getShardingTotalCount());
        result.setCron(liteJobConfig.getTypeConfig().getCoreConfig().getCron());
        result.setShardingItemParameters(liteJobConfig.getTypeConfig().getCoreConfig().getShardingItemParameters());
        result.setJobParameter(liteJobConfig.getTypeConfig().getCoreConfig().getJobParameter());
        result.setMonitorExecution(liteJobConfig.isMonitorExecution());
        result.setMaxTimeDiffSeconds(liteJobConfig.getMaxTimeDiffSeconds());
        result.setMonitorPort(liteJobConfig.getMonitorPort());
        result.setFailover(liteJobConfig.getTypeConfig().getCoreConfig().isFailover());
        result.setMisfire(liteJobConfig.getTypeConfig().getCoreConfig().isMisfire());
        result.setJobShardingStrategyClass(liteJobConfig.getJobShardingStrategyClass());
        result.setDescription(liteJobConfig.getTypeConfig().getCoreConfig().getDescription());
        result.setReconcileIntervalMinutes(liteJobConfig.getReconcileIntervalMinutes());
        result.getJobProperties().setExecutorServiceHandler(liteJobConfig.getTypeConfig().getCoreConfig()
            .getJobProperties().get(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER));
        result.getJobProperties().setJobExceptionHandler(liteJobConfig.getTypeConfig().getCoreConfig()
            .getJobProperties().get(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER));
    }

    private void buildDataflowJobSettings(final Job result, final DataflowJobConfiguration config) {
        result.setStreamingProcess(config.isStreamingProcess());
    }

    private void buildScriptJobSettings(final Job result, final ScriptJobConfiguration config) {
        result.setScriptCommandLine(config.getScriptCommandLine());
    }

    @Override
    public void updateJobSettings(final Job jobSettings) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(jobSettings.getJobName()), "jobName can not be empty.");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(jobSettings.getCron()), "cron can not be empty.");
        Preconditions.checkArgument(jobSettings.getShardingTotalCount() > 0,
            "shardingTotalCount should larger than zero.");
        JobNodePath jobNodePath = new JobNodePath(jobSettings.getJobName());
        regCenter.update(jobNodePath.getConfigNodePath(), LiteJobConfigurationGsonFactory.toJsonForObject(jobSettings));
    }

    @Override
    public void removeJobSettings(final String jobName) {
        regCenter.remove("/" + jobName);
    }
}
