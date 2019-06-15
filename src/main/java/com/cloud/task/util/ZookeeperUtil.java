package com.cloud.task.util;

import org.apache.curator.framework.recipes.cache.TreeCache;

import com.cloud.task.constant.TaskConstants;
import com.cloud.task.handler.ScheduledTaskBuilder;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

import lombok.extern.slf4j.Slf4j;

/**
 * 〈一句话功能简述〉<br> 
 * 〈zookeeper工具类〉
 *
 * @author number68
 * @date 2019/5/10
 * @since 0.1
 */
@Slf4j
public class ZookeeperUtil {
    public static void closeJob(final ZookeeperRegistryCenter registryCenter, ScheduledTaskBuilder scheduledTaskBuilder,
        final String jobName) {
        log.info("errMsg = {}", "Shut down " + jobName + " begin.");
        String jobNodePath = TaskConstants.PATH_DELIMITER + jobName;
        // 关闭所有的监听器
        closeTreeCache(registryCenter, jobNodePath);
        // 关闭定时任务
        scheduledTaskBuilder.closeSpringJobScheduler(jobName);
        // 删除zookeeper job节点
        removeJobNodeIfExisted(registryCenter, jobNodePath);
        log.info("errMsg = {}", "Job " + jobName + " has been shut down because of finish.");
    }

    private static void closeTreeCache(final ZookeeperRegistryCenter registryCenter, final String jobNodePath) {
        TreeCache cache = (TreeCache)registryCenter.getRawCache(jobNodePath);
        cache.close();
    }

    /**
     * 删除作业节点.
     *
     * @param jobNodePath 作业节点路径
     */
    private static void removeJobNodeIfExisted(final ZookeeperRegistryCenter registryCenter, final String jobNodePath) {
        if (isJobNodeExisted(registryCenter, jobNodePath)) {
            registryCenter.remove(jobNodePath);
        }
    }

    /**
     * 判断作业节点是否存在.
     *
     * @param jobNodePath 作业节点路径
     * @return 作业节点是否存在
     */
    private static boolean isJobNodeExisted(final ZookeeperRegistryCenter registryCenter, final String jobNodePath) {
        return registryCenter.isExisted(jobNodePath);
    }
}
