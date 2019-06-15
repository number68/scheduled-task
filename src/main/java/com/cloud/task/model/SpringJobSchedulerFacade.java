package com.cloud.task.model;

import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.internal.server.ServerService;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 〈SpringJobScheduler的包装类〉<br> 
 *
 * @author number68
 * @date 2019/6/4
 * @since 0.1
 */
@AllArgsConstructor
@Getter
public class SpringJobSchedulerFacade {

    private LiteJobConfiguration jobConfiguration;

    private SpringJobScheduler springJobScheduler;

    private ServerService serverService;
}
