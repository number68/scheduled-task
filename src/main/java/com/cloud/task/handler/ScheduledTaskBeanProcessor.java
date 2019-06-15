package com.cloud.task.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cloud.task.annotation.ScheduledTask;
import com.cloud.task.constant.TaskConstants;
import com.cloud.task.listener.ManualScheduledTaskListener;
import com.cloud.task.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import com.cloud.task.model.ShutDownApplicationEvent;
import com.cloud.task.model.TaskScheduler;
import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 *〈定时任务解析器〉<br> 
 *
 * @author number68
 * @date 2019/4/25
 * @since 0.1
 */
@Configuration
@Slf4j
@Order(1)
public class ScheduledTaskBeanProcessor implements ApplicationListener<ApplicationStartedEvent> {

    private String prefix = "task.";

    @Autowired
    private ScheduledTaskBuilder scheduledTaskBuilder;

    @Autowired
    private Environment environment;

    @Autowired
    private ManualScheduledTaskListener manualScheduledTaskListener;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        ConfigurableApplicationContext ctx = event.getApplicationContext();
        Map<String, Object> beanMap = ctx.getBeansWithAnnotation(ScheduledTask.class);
        List<TaskScheduler> taskSchedulers = new ArrayList<>();
        try {
            for (Object confBean : beanMap.values()) {
                taskSchedulers.add(createTaskScheduler((ElasticJob)confBean, (Class<ElasticJob>)(confBean.getClass())));
            }
        } catch (Exception e) {
            closeApplication(ctx, e);
            return;
        }

        try {
            for (TaskScheduler taskScheduler : taskSchedulers) {
                log.info("【" + taskScheduler.getScheduledTask().name() + "】\tinit begin");
                taskScheduler.getSpringJobScheduler().init();
                log.info("【" + taskScheduler.getScheduledTask().name() + "】\tinit success");
            }
        } catch (Exception e) {
            closeApplication(ctx, e);
        }

        if (null != manualScheduledTaskListener) {
            manualScheduledTaskListener.monitorJob();
        }
    }

    private TaskScheduler createTaskScheduler(ElasticJob taskBean, Class<ElasticJob> taskClass) {
        ScheduledTask conf = taskClass.getAnnotation(ScheduledTask.class);
        String taskName = conf.name();
        String cron = getEnvironmentStringValue(taskName, TaskConstants.CRON, conf.cron());
        int shardingTotalCount =
                getEnvironmentIntValue(taskName, TaskConstants.SHARDING_TOTAL_COUNT, conf.shardingTotalCount());
        String shardingItemParameters =
                getEnvironmentStringValue(taskName, TaskConstants.SHARDING_ITEM_PARAMETERS, conf.shardingItemParameters());

        String description = getEnvironmentStringValue(taskName, TaskConstants.DESCRIPTION, conf.description());
        String jobParameter = getEnvironmentStringValue(taskName, TaskConstants.JOB_PARAMETER, conf.jobParameter());
        String jobExceptionHandler =
                getEnvironmentStringValue(taskName, TaskConstants.JOB_EXCEPTION_HANDLER, conf.jobExceptionHandler());
        String executorServiceHandler =
                getEnvironmentStringValue(taskName, TaskConstants.EXECUTOR_SERVICE_HANDLER, conf.executorServiceHandler());
        String jobShardingStrategyClass = getEnvironmentStringValue(taskName, TaskConstants.JOB_SHARDING_STRATEGY_CLASS,
            conf.jobShardingStrategyClass());
        String eventTraceRdbDataSource = getEnvironmentStringValue(taskName, TaskConstants.EVENT_TRACE_RDB_DATA_SOURCE,
            conf.eventTraceRdbDataSource());

        boolean failover = getEnvironmentBooleanValue(taskName, TaskConstants.FAILOVER, conf.failover());
        boolean misfire = getEnvironmentBooleanValue(taskName, TaskConstants.MISFIRE, conf.misfire());
        boolean overwrite = getEnvironmentBooleanValue(taskName, TaskConstants.OVERWRITE, conf.overwrite());
        boolean disabled = getEnvironmentBooleanValue(taskName, TaskConstants.DISABLED, conf.disabled());

        boolean monitorExecution =
                getEnvironmentBooleanValue(taskName, TaskConstants.MONITOR_EXECUTION, conf.monitorExecution());
        int monitorPort = getEnvironmentIntValue(taskName, TaskConstants.MONITOR_PORT, conf.monitorPort());

        boolean streamingProcess =
                getEnvironmentBooleanValue(taskName, TaskConstants.STREAMING_PROCESS, conf.streamingProcess());

        int maxTimeDiffSeconds =
                getEnvironmentIntValue(taskName, TaskConstants.MAX_TIME_DIFF_SECONDS, conf.maxTimeDiffSeconds());
        int reconcileIntervalMinutes =
                getEnvironmentIntValue(taskName, TaskConstants.RECONCILE_INTERVAL_MINUTES, conf.reconcileIntervalMinutes());

        // 核心配置
        JobCoreConfiguration coreConfig = JobCoreConfiguration.newBuilder(taskName, cron, shardingTotalCount)
            .shardingItemParameters(shardingItemParameters).description(description).failover(failover)
            .jobParameter(jobParameter).misfire(misfire)
            .jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), jobExceptionHandler)
            .jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), executorServiceHandler)
            .build();

        JobTypeConfiguration typeConfig = null;
        if (SimpleJob.class.isAssignableFrom(taskClass)) {
            typeConfig = new SimpleJobConfiguration(coreConfig, taskClass.getCanonicalName());
        } else if (DataflowJob.class.isAssignableFrom(taskClass)) {
            typeConfig = new DataflowJobConfiguration(coreConfig, taskClass.getCanonicalName(), streamingProcess);
        } else {
            String errorMsg = "Invalid task config, task name=" + taskName;
            throw new RuntimeException(errorMsg);
        }

        LiteJobConfiguration jobConfig = LiteJobConfiguration.newBuilder(typeConfig).overwrite(overwrite)
            .disabled(disabled).monitorPort(monitorPort).monitorExecution(monitorExecution)
            .maxTimeDiffSeconds(maxTimeDiffSeconds).jobShardingStrategyClass(jobShardingStrategyClass)
            .reconcileIntervalMinutes(reconcileIntervalMinutes).build();

        // 任务执行日志数据源，以名称获取
        JobEventConfiguration jobEventRdbConfiguration = null;
        if (StringUtils.hasText(eventTraceRdbDataSource)) {
            jobEventRdbConfiguration = SpringUtil.getBean(eventTraceRdbDataSource, JobEventConfiguration.class);
        }

        List<ElasticJobListener> elasticJobListeners = getTargetElasticJobListeners(conf);
        return new TaskScheduler(
            scheduledTaskBuilder.buildScheduledTask(taskBean, jobConfig, jobEventRdbConfiguration, elasticJobListeners),
            conf);
    }

    private List<ElasticJobListener> getTargetElasticJobListeners(ScheduledTask conf) {
        List<ElasticJobListener> result = new ArrayList<>();
        String listener = getEnvironmentStringValue(conf.name(), TaskConstants.LISTENER, conf.listener());
        if (StringUtils.hasText(listener)) {
            ElasticJobListener taskListener = SpringUtil.getBean(listener, ElasticJobListener.class);
            if (null != taskListener) {
                result.add(taskListener);
            }
        }

        String distributedListener =
                getEnvironmentStringValue(conf.name(), TaskConstants.DISTRIBUTED_LISTENER, conf.distributedListener());
        if (StringUtils.hasText(distributedListener)) {
            ElasticJobListener distributedTaskListener =
                SpringUtil.getBean(distributedListener, ElasticJobListener.class);
            if (null != distributedTaskListener) {
                result.add(distributedTaskListener);
            }
        }
        return result;

    }

    /**
     * 获取配置中的任务属性值，environment没有就用注解中的值
     * @param jobName		任务名称
     * @param fieldName		属性名称
     * @param defaultValue  默认值
     * @return
     */
    private String getEnvironmentStringValue(String jobName, String fieldName, String defaultValue) {
        String key = prefix + jobName + "." + fieldName;
        String value = environment.getProperty(key);
        if (StringUtils.hasText(value)) {
            return value;
        }
        return defaultValue;
    }

    private int getEnvironmentIntValue(String jobName, String fieldName, int defaultValue) {
        String key = prefix + jobName + "." + fieldName;
        String value = environment.getProperty(key);
        if (StringUtils.hasText(value)) {
            return Integer.valueOf(value);
        }
        return defaultValue;
    }

    private long getEnvironmentLongValue(String jobName, String fieldName, long defaultValue) {
        String key = prefix + jobName + "." + fieldName;
        String value = environment.getProperty(key);
        if (StringUtils.hasText(value)) {
            return Long.valueOf(value);
        }
        return defaultValue;
    }

    private boolean getEnvironmentBooleanValue(String jobName, String fieldName, boolean defaultValue) {
        String key = prefix + jobName + "." + fieldName;
        String value = environment.getProperty(key);
        if (StringUtils.hasText(value)) {
            return Boolean.valueOf(value);
        }
        return defaultValue;
    }

    protected void closeApplication(ConfigurableApplicationContext ctx, Exception e) {
        ctx.publishEvent(new ShutDownApplicationEvent(ctx.getApplicationName(), ctx, e));
    }
}
