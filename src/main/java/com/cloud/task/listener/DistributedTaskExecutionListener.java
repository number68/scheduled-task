package com.cloud.task.listener;

import com.cloud.task.handler.ScheduledTaskBuilder;
import org.springframework.util.StringUtils;

import com.cloud.task.constant.TaskConstants;
import com.cloud.task.util.ZookeeperUtil;
import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.AbstractDistributeOnceElasticJobListener;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

import lombok.extern.slf4j.Slf4j;

/**
 *〈DistributedTaskExecutionListener〉<br>
 * 一次性作业执行监听器，最后执行
 *
 * @author number68
 * @date 2019/4/24
 * @since 0.1
 */
@Slf4j
public class DistributedTaskExecutionListener extends AbstractDistributeOnceElasticJobListener {

    private ZookeeperRegistryCenter registryCenter;

    private ScheduledTaskBuilder scheduledTaskBuilder;

    public DistributedTaskExecutionListener(ZookeeperRegistryCenter registryCenter,
        ScheduledTaskBuilder scheduledTaskBuilder) {
        // 等待所有任务启动的超时时间设置为3分钟
        super(180000L, 0L);
        this.registryCenter = registryCenter;
        this.scheduledTaskBuilder = scheduledTaskBuilder;
    }

    @Override
    public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {
        log.info("All distributed job:{} shards have started execution.", shardingContexts.getJobName());
    }

    @Override
    public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {
        log.info("All distributed job:{} shards have been finished successfully.", shardingContexts.getJobName());

        String jobParameter = shardingContexts.getJobParameter();
        if (!StringUtils.isEmpty(jobParameter)
                && jobParameter.contains(TaskConstants.JOB_PARAMETER_DELIMETER + TaskConstants.RUN_ONCE)) {
            // 将一次性作业删除
            String jobName = shardingContexts.getJobName();
            ZookeeperUtil.closeJob(registryCenter, scheduledTaskBuilder, jobName);
        }
    }
}
