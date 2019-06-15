package com.cloud.task.task;

import java.util.List;

import com.cloud.task.annotation.ScheduledTask;
import org.springframework.util.CollectionUtils;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;

import lombok.extern.slf4j.Slf4j;

/**
 *〈activity task〉<br> 
 *
 * @author number68
 * @date 2019/4/24
 * @since 0.1
 */
@ScheduledTask(name = "taskExample", cron = "0 */10 * * * ?", shardingTotalCount = 2, overwrite = true)
@Slf4j
public class TaskExample implements DataflowJob {
    /**
     * 获取待处理数据.
     *
     * @param shardingContext 分片上下文
     * @return 待处理的数据集合
     */
    @Override
    public List fetchData(ShardingContext shardingContext) {
        log.info("Fetch actity data");
        return null;
    }

    /**
     * process data
     *
     * @param shardingContext 分片上下文
     * @param data 待处理数据集合
     */
    @Override
    public void processData(ShardingContext shardingContext, List data) {
        // print activity task info
        log.info("任务名={}, 片数={}, sharding item={}, data size={}", shardingContext.getJobName(),
            shardingContext.getShardingTotalCount(), shardingContext.getShardingItem(),
            CollectionUtils.isEmpty(data) ? 0 : data.size());
    }
}
