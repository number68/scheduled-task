package com.cloud.task.model;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.Data;

/**
 *〈关闭应用事件〉<br> 
 *
 * @author number68
 * @date 2019/4/25
 * @since 0.1
 */
@Data
public class ShutDownApplicationEvent extends ApplicationEvent {
    private ConfigurableApplicationContext context;
    private Throwable exception;

    public ShutDownApplicationEvent(String applicationName, ConfigurableApplicationContext context,
        Throwable exception) {
        super(applicationName);
        this.context = context;
        this.exception = exception;
    }
}
