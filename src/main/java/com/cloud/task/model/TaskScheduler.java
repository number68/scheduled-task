package com.cloud.task.model;

import com.cloud.task.annotation.ScheduledTask;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;

import lombok.Data;

/**
 *〈SpringJobScheduler 包装类〉<br> 
 *
 * @author number68
 * @date 2019/4/25
 * @since 0.1
 */
@Data
public class TaskScheduler {
    private SpringJobScheduler springJobScheduler;

    private ScheduledTask scheduledTask;

    public TaskScheduler(SpringJobScheduler springJobScheduler, ScheduledTask scheduledTask) {
        this.springJobScheduler = springJobScheduler;
        this.scheduledTask = scheduledTask;
    }
}
