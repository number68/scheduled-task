package com.cloud.task.impl;

import java.util.List;

import com.cloud.task.api.IJobOperateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.lite.internal.storage.JobNodePath;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * 操作作业的实现类.
 *
 */
@Service
public final class JobOperateServiceImpl implements IJobOperateService {

    @Autowired
    private CoordinatorRegistryCenter regCenter;

    @Override
    public void trigger(final Optional<String> jobName, final Optional<String> serverIp) {
        if (jobName.isPresent()) {
            JobNodePath jobNodePath = new JobNodePath(jobName.get());
            for (String each : regCenter.getChildrenKeys(jobNodePath.getInstancesNodePath())) {
                regCenter.persist(jobNodePath.getInstanceNodePath(each), "TRIGGER");
            }
        }
    }

    @Override
    public void disable(final Optional<String> jobName, final Optional<String> serverIp) {
        disableOrEnableJobs(jobName, serverIp, true);
    }

    @Override
    public void enable(final Optional<String> jobName, final Optional<String> serverIp) {
        disableOrEnableJobs(jobName, serverIp, false);
    }

    private void disableOrEnableJobs(final Optional<String> jobName, final Optional<String> serverIp,
        final boolean disabled) {
        Preconditions.checkArgument(jobName.isPresent() || serverIp.isPresent(),
            "At least indicate jobName or serverIp.");
        if (jobName.isPresent() && serverIp.isPresent()) {
            persistDisabledOrEnabledJob(jobName.get(), serverIp.get(), disabled);
        } else if (jobName.isPresent()) {
            JobNodePath jobNodePath = new JobNodePath(jobName.get());
            for (String each : regCenter.getChildrenKeys(jobNodePath.getServerNodePath())) {
                if (disabled) {
                    regCenter.persist(jobNodePath.getServerNodePath(each), "DISABLED");
                } else {
                    regCenter.persist(jobNodePath.getServerNodePath(each), "");
                }
            }
        } else if (serverIp.isPresent()) {
            List<String> jobNames = regCenter.getChildrenKeys("/");
            for (String each : jobNames) {
                if (regCenter.isExisted(new JobNodePath(each).getServerNodePath(serverIp.get()))) {
                    persistDisabledOrEnabledJob(each, serverIp.get(), disabled);
                }
            }
        }
    }

    private void persistDisabledOrEnabledJob(final String jobName, final String serverIp, final boolean disabled) {
        JobNodePath jobNodePath = new JobNodePath(jobName);
        String serverNodePath = jobNodePath.getServerNodePath(serverIp);
        if (disabled) {
            regCenter.persist(serverNodePath, "DISABLED");
        } else {
            regCenter.persist(serverNodePath, "");
        }
    }

    @Override
    public void shutdown(final Optional<String> jobName, final Optional<String> serverIp) {
        Preconditions.checkArgument(jobName.isPresent() || serverIp.isPresent(),
            "At least indicate jobName or serverIp.");
        if (jobName.isPresent() && serverIp.isPresent()) {
            JobNodePath jobNodePath = new JobNodePath(jobName.get());
            for (String each : regCenter.getChildrenKeys(jobNodePath.getInstancesNodePath())) {
                if (serverIp.get().equals(each.split("@-@")[0])) {
                    regCenter.remove(jobNodePath.getInstanceNodePath(each));
                }
            }
        } else if (jobName.isPresent()) {
            JobNodePath jobNodePath = new JobNodePath(jobName.get());
            for (String each : regCenter.getChildrenKeys(jobNodePath.getInstancesNodePath())) {
                regCenter.remove(jobNodePath.getInstanceNodePath(each));
            }
        } else if (serverIp.isPresent()) {
            List<String> jobNames = regCenter.getChildrenKeys("/");
            for (String job : jobNames) {
                JobNodePath jobNodePath = new JobNodePath(job);
                List<String> instances = regCenter.getChildrenKeys(jobNodePath.getInstancesNodePath());
                for (String each : instances) {
                    if (serverIp.get().equals(each.split("@-@")[0])) {
                        regCenter.remove(jobNodePath.getInstanceNodePath(each));
                    }
                }
            }
        }
    }

    @Override
    public void remove(final Optional<String> jobName, final Optional<String> serverIp) {
        shutdown(jobName, serverIp);
        if (jobName.isPresent() && serverIp.isPresent()) {
            regCenter.remove(new JobNodePath(jobName.get()).getServerNodePath(serverIp.get()));
        } else if (jobName.isPresent()) {
            JobNodePath jobNodePath = new JobNodePath(jobName.get());
            List<String> servers = regCenter.getChildrenKeys(jobNodePath.getServerNodePath());
            for (String each : servers) {
                regCenter.remove(jobNodePath.getServerNodePath(each));
            }
        } else if (serverIp.isPresent()) {
            List<String> jobNames = regCenter.getChildrenKeys("/");
            for (String each : jobNames) {
                regCenter.remove(new JobNodePath(each).getServerNodePath(serverIp.get()));
            }
        }
    }
}
