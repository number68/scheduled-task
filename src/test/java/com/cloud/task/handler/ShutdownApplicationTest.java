package com.cloud.task.handler;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import com.cloud.task.ScheduledTaskTestApplication;
import com.cloud.task.util.SpringUtil;

/**
 *〈主动关闭应用测试〉<br>
 *
 * @author number68
 * @date 2019/4/26
 * @since 0.1
 */
public class ShutdownApplicationTest extends ScheduledTaskTestApplication {
    @Autowired
    private ScheduledTaskBeanProcessor scheduledTaskBeanProcessor;

    @Test
    public void shutdown() {
        scheduledTaskBeanProcessor.closeApplication((ConfigurableApplicationContext)SpringUtil.getApplicationContext(),
            new RuntimeException("I want to close Application."));
    }
}
