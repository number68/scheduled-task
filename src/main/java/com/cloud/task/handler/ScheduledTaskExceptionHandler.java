package com.cloud.task.handler;

import com.dangdang.ddframe.job.executor.handler.JobExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 *〈ScheduledTask 异常处理器〉<br>
 *
 * @author number68
 * @date 2019/4/26
 * @since 0.1
 */
@Slf4j
public final class ScheduledTaskExceptionHandler implements JobExceptionHandler {

    @Override
    public void handleException(final String jobName, final Throwable cause) {
        log.error("Exception occurs in job {} processing, errMsg = {}, stackTrace = ", jobName, cause.getMessage(),
            cause);
    }
}
