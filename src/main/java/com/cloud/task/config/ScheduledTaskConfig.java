package com.cloud.task.config;

import javax.sql.DataSource;

import com.cloud.task.handler.ScheduledTaskBuilder;
import com.cloud.task.listener.DistributedTaskExecutionListener;
import com.cloud.task.listener.ScheduledTaskExecutionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

/**
 *〈scheduled task config〉<br>
 *
 * @author number68
 * @date 2019/4/24
 * @since 0.1
 */
@Configuration
public class ScheduledTaskConfig {
    /**
     * 将作业运行的痕迹持久化到DB
     *
     * @return
     */
    @Bean("defaultJobEventConfiguration")
    public JobEventConfiguration jobEventConfiguration(DataSource dataSource) {
        return new JobEventRdbConfiguration(dataSource);
    }

    @Bean("defaultElasticJobListener")
    public ElasticJobListener elasticJobListener() {
        return new ScheduledTaskExecutionListener();
    }

    @Bean("distributedElasticJobListener")
    public ElasticJobListener distributedElasticJobListener(ZookeeperRegistryCenter registryCenter,
        ScheduledTaskBuilder scheduledTaskBuilder) {
        return new DistributedTaskExecutionListener(registryCenter, scheduledTaskBuilder);
    }
}
