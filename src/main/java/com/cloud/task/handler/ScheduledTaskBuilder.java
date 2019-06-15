package com.cloud.task.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cloud.task.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.cloud.task.constant.TaskConstants;
import com.cloud.task.model.Job;
import com.cloud.task.model.SpringJobSchedulerFacade;
import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.internal.server.ServerService;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

import lombok.extern.slf4j.Slf4j;

/**
 *〈定时任务处理器〉<br>
 *
 * @author number68
 * @date 2019/4/24
 * @since 0.1
 */
@Configuration
@Slf4j
public class ScheduledTaskBuilder {
    private Map<String, SpringJobSchedulerFacade> schedulerMap = new ConcurrentHashMap<>();

    @Autowired
    private ZookeeperRegistryCenter registryCenter;

    @Autowired
    @Qualifier("defaultJobEventConfiguration")
    private JobEventConfiguration jobEventConfiguration;

    @Autowired
    @Qualifier("defaultElasticJobListener")
    private ElasticJobListener elasticJobListener;

    @Autowired
    @Qualifier("distributedElasticJobListener")
    private ElasticJobListener distributedElasticJobListener;

    public Map<String, SpringJobSchedulerFacade> getSchedulerMap() {
        return schedulerMap;
    }

    public SpringJobScheduler buildScheduledTask(final ElasticJob elasticJob, final LiteJobConfiguration jobConfig,
        JobEventConfiguration jobEventRdbConfiguration, List<ElasticJobListener> elasticJobListeners) {
        if (!elasticJobListeners.contains(this.elasticJobListener)) {
            elasticJobListeners.add(this.elasticJobListener);
        }

        if (!elasticJobListeners.contains(this.distributedElasticJobListener)) {
            elasticJobListeners.add(this.distributedElasticJobListener);
        }

        if (null == jobEventRdbConfiguration) {
            jobEventRdbConfiguration = this.jobEventConfiguration;
        }

        ElasticJobListener[] elasticJobListenersArray = new ElasticJobListener[elasticJobListeners.size()];
        SpringJobScheduler springJobScheduler = new SpringJobScheduler(elasticJob, registryCenter, jobConfig,
            jobEventRdbConfiguration, elasticJobListeners.toArray(elasticJobListenersArray));
        schedulerMap.put(jobConfig.getJobName(), new SpringJobSchedulerFacade(jobConfig, springJobScheduler,
            new ServerService(registryCenter, jobConfig.getJobName())));
        return springJobScheduler;
    }

    /**
     * 构造手动定时任务并触发
     *
     * @param job
     */
    public void buildManualScheduledTaskAndInit(Job job) throws Exception {
        buildManualScheduledTaskAndInit(job, false);
    }

    /**
     * 构造手动执行任务并触发
     *
     * @param job
     * @param runOnce 仅执行一次的任务
     * @throws Exception
     */
    public void buildManualScheduledTaskAndInit(Job job, boolean runOnce) throws Exception {
        if (runOnce) {
            String jobParameter = job.getJobParameter();
            if (StringUtils.isEmpty(jobParameter)
                || !jobParameter.contains(TaskConstants.JOB_PARAMETER_DELIMETER + TaskConstants.RUN_ONCE)) {
                jobParameter = jobParameter + TaskConstants.JOB_PARAMETER_DELIMETER + TaskConstants.RUN_ONCE;
                job.setJobParameter(jobParameter);
            }
        }

        JobCoreConfiguration coreConfig =
            JobCoreConfiguration.newBuilder(job.getJobName(), job.getCron(), job.getShardingTotalCount())
                .shardingItemParameters(job.getShardingItemParameters()).description(job.getDescription())
                .failover(job.isFailover()).jobParameter(job.getJobParameter()).misfire(job.isMisfire())
                .jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(),
                    job.getJobProperties().getJobExceptionHandler())
                .jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(),
                    job.getJobProperties().getExecutorServiceHandler())
                .build();

        JobTypeConfiguration typeConfig = null;
        String jobType = job.getJobType().toUpperCase();
        switch (jobType) {
            case TaskConstants.SIMPLE_JOB_TYPE:
                typeConfig = new SimpleJobConfiguration(coreConfig, job.getJobClass());
                break;
            case TaskConstants.DATAFLOW_JOB_TYPE:
                typeConfig = new DataflowJobConfiguration(coreConfig, job.getJobClass(), job.isStreamingProcess());
                break;
            case TaskConstants.SCRIPT_JOB_TYPE:
                typeConfig = new ScriptJobConfiguration(coreConfig, job.getScriptCommandLine());
                break;
            default:
                String errorMsg = "Invalid job config, job name=" + job.getJobName();
                throw new RuntimeException(errorMsg);
        }

        LiteJobConfiguration jobConfig = LiteJobConfiguration.newBuilder(typeConfig).overwrite(job.isOverwrite())
            .disabled(job.isDisabled()).monitorPort(job.getMonitorPort()).monitorExecution(job.isMonitorExecution())
            .maxTimeDiffSeconds(job.getMaxTimeDiffSeconds()).jobShardingStrategyClass(job.getJobShardingStrategyClass())
            .reconcileIntervalMinutes(job.getReconcileIntervalMinutes()).build();

        // 任务执行日志数据源，以名称获取
        JobEventConfiguration jobEventRdbConfiguration = null;
        if (StringUtils.hasText(job.getEventTraceRdbDataSource())) {
            jobEventRdbConfiguration =
                SpringUtil.getBean(job.getEventTraceRdbDataSource(), JobEventConfiguration.class);
        }

        List<ElasticJobListener> elasticJobListeners = getTargetElasticJobListeners(job);
        buildScheduledTask((ElasticJob)(Class.forName(job.getJobClass()).newInstance()), jobConfig,
            jobEventRdbConfiguration, elasticJobListeners).init();
        log.info("【" + job.getJobName() + "】\t" + job.getJobClass() + "\tinit success");
    }

    public void closeSpringJobScheduler(String jobName) {
        SpringJobSchedulerFacade springJobSchedulerFacade = schedulerMap.remove(jobName);
        if (null != springJobSchedulerFacade) {
            springJobSchedulerFacade.getSpringJobScheduler().getSchedulerFacade().shutdownInstance();
        }
    }

    private List<ElasticJobListener> getTargetElasticJobListeners(Job job) {
        List<ElasticJobListener> result = new ArrayList<>();
        String listener = job.getListener();
        if (StringUtils.hasText(listener)) {
            ElasticJobListener taskListener = SpringUtil.getBean(listener, ElasticJobListener.class);
            if (null != taskListener) {
                result.add(taskListener);
            }
        }

        String distributedListener = job.getDistributedListener();
        if (StringUtils.hasText(distributedListener)) {
            ElasticJobListener distributedTaskListener =
                SpringUtil.getBean(distributedListener, ElasticJobListener.class);
            if (null != distributedTaskListener) {
                result.add(distributedTaskListener);
            }
        }
        return result;
    }
}
