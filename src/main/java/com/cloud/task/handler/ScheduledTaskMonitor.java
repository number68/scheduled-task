package com.cloud.task.handler;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cloud.task.model.SpringJobSchedulerFacade;
import com.dangdang.ddframe.job.lite.internal.schedule.JobRegistry;

import lombok.extern.slf4j.Slf4j;

/**
 *〈定时任务监控预警〉<br>
 *
 * @author number68
 * @date 2019/06/03
 * @since 0.1
 */
@Configuration
@Slf4j
@Order(2)
public class ScheduledTaskMonitor implements ApplicationListener<ApplicationStartedEvent> {

    private static String QUERY_JOB_STATUS_SQL =
        "select id, job_name, task_id, sharding_item, state, message, creation_time from job_status_trace_log where job_name = ? and creation_time between ? and ? limit 1";

    @Autowired
    private ScheduledTaskBuilder scheduledTaskBuilder;

    @Autowired
    private DataSource dataSource;

    @Value("${scheduledTask.monitor.enable:false}")
    private Boolean scheduledTaskMonitorEnabled;

    /** The executor service for managing the task running status. */
    private ScheduledExecutorService executorService;

    @Value("${scheduledTask.monitor.time.interval:3}")
    private int timeBetweenMonitorInterval;

    private JdbcTemplate jdbcTemplate;

    /**
     * 上次监控时间
     */
    private AtomicLong lastMonitorTime;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if (!scheduledTaskMonitorEnabled) {
            return;
        }

        lastMonitorTime = new AtomicLong(System.currentTimeMillis());
        executorService = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("ScheduledTaskMonitor"));
        jdbcTemplate = new JdbcTemplate(dataSource);

        executorService.scheduleAtFixedRate(() -> {
            Date thisMonitorDate = new Date();
            try {
                Map<String, SpringJobSchedulerFacade> schedulerMap = scheduledTaskBuilder.getSchedulerMap();
                for (Map.Entry<String, SpringJobSchedulerFacade> entry : schedulerMap.entrySet()) {
                    String jobName = entry.getKey();
                    log.info("ScheduledTaskMonitor is dealing job:" + jobName + " begin");
                    SpringJobSchedulerFacade springJobSchedulerFacade = entry.getValue();
                    if (!JobRegistry.getInstance().isShutdown(jobName) && springJobSchedulerFacade.getServerService()
                        .isAvailableServer(JobRegistry.getInstance().getJobInstance(jobName).getIp())) {
                        String cron =
                            springJobSchedulerFacade.getJobConfiguration().getTypeConfig().getCoreConfig().getCron();
                        CronExpression cronEx = new CronExpression(cron);
                        Date lastMonitorDate = new Date(lastMonitorTime.get());
                        Date nextTriggerDate = cronEx.getTimeAfter(lastMonitorDate);
                        if (nextTriggerDate.before(thisMonitorDate)) {
                            if (findJobStatusTraceEventsCount(jobName, lastMonitorDate, thisMonitorDate) == 0) {
                                log.error("errMsg = {}",
                                    "ScheduledTaskMonitor job:" + jobName + " started failed, please check");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("errMsg = {}, stackTrace = ", "ScheduledTaskMonitor failed", e);
            } finally {
                lastMonitorTime.set(thisMonitorDate.getTime());
            }
        }, timeBetweenMonitorInterval, timeBetweenMonitorInterval, TimeUnit.MINUTES);
    }

    /**
     * 获取作业执行状态记录条数
     * @return
     */
    private int findJobStatusTraceEventsCount(String jobName, Date startTime, Date endTime) {
        Map<String, Object> resutlMap = jdbcTemplate.queryForMap(QUERY_JOB_STATUS_SQL, jobName, startTime, endTime);
        // 查询数据库是否有job status info
        return resutlMap.size();
    }

    private class DaemonThreadFactory implements ThreadFactory {

        private AtomicInteger threadNo = new AtomicInteger(1);
        private final String nameStart;

        public DaemonThreadFactory(String poolName) {
            nameStart = poolName + "-";
        }

        public Thread newThread(Runnable r) {
            String threadName = nameStart + threadNo.getAndIncrement();
            Thread newThread = new Thread(r, threadName);
            newThread.setDaemon(true);
            if (newThread.getPriority() != Thread.NORM_PRIORITY) {
                newThread.setPriority(Thread.NORM_PRIORITY);
            }
            return newThread;
        }

    }
}
