# scheduled-task
分布式定时任务，封装了elastic-job-lite&amp;elastic-job-lite-console，支持手动触发任务，支持一次性任务
![image](https://github.com/number68/scheduled-task/blob/master/images/console.jpg)

# how to build
- create your db and change application.yml about db cofiguration (include datasource and user and password) accordingly.
- config zookeeper serviceLists in application.yml.

# how to create scheduled task
- see TaskExample class, you can extend DataflowJob or SimpleJob class on your business. The scheduled tasks register into quartz (in jvm) and zookeeper (for distributed, search elastic-job for more information) when application starts.

# how to manually create scheduled task or runOnce scheduled task  
- call ScheduledTaskBuilder.buildManualScheduledTaskAndInit to create task manually(support runOnce).
