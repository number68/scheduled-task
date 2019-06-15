package com.cloud.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import lombok.extern.slf4j.Slf4j;

/**
 * <功能描述><br/>
 *
 * @author zwl
 * @create 2019/03/26 12:07:57
 * @since 0.1
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ServletComponentScan
@Slf4j
public class ScheduledTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduledTaskApplication.class, args);

        log.info("------------scheduled-task started successfully-------------");
    }

}
