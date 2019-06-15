package com.cloud.task.listener;

import java.lang.management.ManagementFactory;

import com.cloud.task.util.JSONUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.cloud.task.constant.TaskConstants;
import com.cloud.task.handler.ScheduledTaskBuilder;
import com.cloud.task.model.Job;
import com.cloud.task.util.ZookeeperUtil;
import com.dangdang.ddframe.job.lite.internal.schedule.JobRegistry;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.dangdang.ddframe.job.util.env.IpUtils;

import lombok.extern.slf4j.Slf4j;

/**
 *〈手动添加定时任务监听器，用于将节点同步到其他实例〉<br> 
 *
 * @author number68
 * @date 2019/4/25
 * @since 0.1
 */
@Configuration
@Slf4j
public class ManualScheduledTaskListener {
    private static final String DELIMITER = "@-@";

    @Autowired
    private ZookeeperRegistryCenter registryCenter;

    @Autowired
    private ScheduledTaskBuilder scheduledTaskBuilder;

    /**
     * 开启任务监听,当有任务添加时，监听zk中的数据增加，自动在其他节点也初始化该任务
     */
    public void monitorJob() {
        CuratorFramework client = registryCenter.getClient();
        PathChildrenCache childrenCache = new PathChildrenCache(client, "/", true);
        PathChildrenCacheListener childrenCacheListener =
            (CuratorFramework curatorFramework, PathChildrenCacheEvent event) -> {
                switch (event.getType()) {
                    // 命名空间下增加子结点触发
                    case CHILD_ADDED:
                        String addedJobRootPath = event.getData().getPath();
                        String addedJobName =
                            addedJobRootPath.substring(addedJobRootPath.lastIndexOf(TaskConstants.PATH_DELIMITER) + 1);
                        if (null != JobRegistry.getInstance().getJobInstance(addedJobName)) {
                            break;
                        }

                        String configNodePath = addedJobRootPath + TaskConstants.CONFIG_PATH;
                        if (!registryCenter.isExisted(configNodePath)) {
                            log.warn("Config node:{} has been deleted, this job should be a run once task.",
                                configNodePath);
                            break;
                        }

                        String config = new String(client.getData().forPath(configNodePath));
                        try {
                            Job newJob = JSONUtil.parseObject(config, Job.class);
                            boolean runOnce = false;
                            String newJobParameter = newJob.getJobParameter();
                            if (!StringUtils.isEmpty(newJobParameter) && newJobParameter
                                .contains(TaskConstants.JOB_PARAMETER_DELIMETER + TaskConstants.RUN_ONCE)) {
                                runOnce = true;
                            }
                            scheduledTaskBuilder.buildManualScheduledTaskAndInit(newJob, runOnce);
                        } catch (Exception e) {
                            log.warn("Manually add job:{} failed, errMsg = {}, stackTrace = ", addedJobName,
                                e.getMessage(), e);
                            break;
                        }
                        log.info("Job:{} did not have running instance in this jvm, and has been started by listener.",
                            addedJobName);
                        break;
                    case CHILD_REMOVED:
                        String removedJobRootPath = event.getData().getPath();
                        String removedJobName = removedJobRootPath
                            .substring(removedJobRootPath.lastIndexOf(TaskConstants.PATH_DELIMITER) + 1);
                        if (!JobRegistry.getInstance().isShutdown(removedJobName)) {
                            log.info("Job:{} has been deleted, but it still exists in this instance.",
                                removedJobRootPath);
                            ZookeeperUtil.closeJob(registryCenter, scheduledTaskBuilder, removedJobName);
                        }
                        break;
                    default:
                        break;
                }
            };

        childrenCache.getListenable().addListener(childrenCacheListener);
        try {
            childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        } catch (Exception e) {
            log.error("MonitorJob failed, errMsg = {}, stackTrace = ", e.getMessage(), e);
        }
    }

    /**
     * 生成当前jvm的elaticJob实例名 ip+进程号
     * @return
     */
    private static String createJobInstanceId() {
        return IpUtils.getIp() + DELIMITER + ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }
}
