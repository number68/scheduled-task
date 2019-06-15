package com.cloud.task.task;

import com.cloud.task.ScheduledTaskTestApplication;
import com.cloud.task.constant.TaskConstants;
import com.cloud.task.handler.ScheduledTaskBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cloud.task.model.Job;
import com.dangdang.ddframe.job.lite.internal.schedule.JobRegistry;

/**
 *〈手动触发任务测试〉<br> 
 *
 * @author number68
 * @date 2019/4/26
 * @since 0.1
 */
public class ManuallyAddTaskTest extends ScheduledTaskTestApplication {
    @Autowired
    private ScheduledTaskBuilder scheduledTaskBuilder;

    @Test
    public void manuallyAddTask() {
        Job job = new Job();
        job.setJobName("ManualActivityTest");
        job.setJobClass("TaskExample");
        job.setCron("0 */5 * * * ?");
        job.setOverwrite(true);
        job.setJobType(TaskConstants.DATAFLOW_JOB_TYPE);
        job.setShardingTotalCount(2);
        try {
            scheduledTaskBuilder.buildManualScheduledTaskAndInit(job);
            assert JobRegistry.getInstance().isJobRunning(job.getJobName());
        } catch (Exception e) {
            e.printStackTrace();
            assert 1 != 1;
        }
    }
}
