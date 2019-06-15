package com.cloud.task.listener;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;

import lombok.extern.slf4j.Slf4j;

/**
 *〈ScheduledTaskListener〉<br>
 *
 * @author number68
 * @date 2019/4/24
 * @since 0.1
 */
@Slf4j
public class ScheduledTaskExecutionListener implements ElasticJobListener {

    public ScheduledTaskExecutionListener() {}

    @Override
    public void beforeJobExecuted(ShardingContexts shardingContexts) {
        log.info("Job:{} shards are starting execution.", shardingContexts.getJobName());
    }

    @Override
    public void afterJobExecuted(ShardingContexts shardingContexts) {
        log.info("Congratulations, Job:{} shards have been finished successfully in this instance.",
            shardingContexts.getJobName());
    }
}
