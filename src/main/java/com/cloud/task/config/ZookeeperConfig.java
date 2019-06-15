package com.cloud.task.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

import lombok.Getter;

/**
 * 〈zookeeper config class〉<br>
 *
 * @author number68
 * @date 2019/4/23
 * @since 0.1
 */
@Configuration
@Getter
public class ZookeeperConfig {
    @Value("${zookeeper.serviceLists}")
    private String serviceLists;

    @Value("${zookeeper.namespace}")
    private String nameSpace;

    @Value("${zookeeper.baseSleepTimeMilliseconds}")
    private int baseSleepTimeMilliseconds;

    @Value("${zookeeper.maxSleepTimeMilliseconds}")
    private int maxSleepTimeMilliseconds;

    @Value("${zookeeper.maxRetries}")
    private int maxRetries;

    @Bean
    public ZookeeperConfiguration zkConfig() {
        ZookeeperConfiguration configuration = new ZookeeperConfiguration(serviceLists, nameSpace);
        configuration.setBaseSleepTimeMilliseconds(baseSleepTimeMilliseconds);
        configuration.setMaxSleepTimeMilliseconds(maxSleepTimeMilliseconds);
        configuration.setMaxRetries(maxRetries);
        return configuration;
    }

    /**
     * zookeeper registry center bean
     * @return
     */
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter(ZookeeperConfiguration configuration) {
        return new ZookeeperRegistryCenter(configuration);
    }
}
