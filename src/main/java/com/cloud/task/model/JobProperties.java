package com.cloud.task.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 *〈JobProperties，用于注册zookeeper节点信息〉<br> 
 *
 * @author number68
 * @date 2019/4/25
 * @since 0.1
 */
@Data
public class JobProperties {

    /**
     * 自定义异常处理类
     * @return
     */
    @JSONField(name = "job_exception_handler")
    @SerializedName("job_exception_handler")
    private String jobExceptionHandler = "ScheduledTaskExceptionHandler";

    /**
     * 自定义业务处理线程池
     * @return
     */
    @JSONField(name = "executor_service_handler")
    @SerializedName("executor_service_handler")
    private String executorServiceHandler =
        "com.dangdang.ddframe.job.executor.handler.impl.DefaultExecutorServiceHandler";

}
