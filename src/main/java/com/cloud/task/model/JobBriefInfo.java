package com.cloud.task.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 作业简明信息对象.
 *
 */
@Data
public final class JobBriefInfo implements Serializable, Comparable<JobBriefInfo> {

    private static final long serialVersionUID = 8405751873086755148L;

    private String jobName;

    private JobStatus status;

    private String description;

    private String cron;

    private int instanceCount;

    private int shardingTotalCount;

    @Override
    public int compareTo(final JobBriefInfo o) {
        return getJobName().compareTo(o.getJobName());
    }

    /**
     * 作业状态.
     *
     * @author caohao
     */
    public enum JobStatus {
        OK, CRASHED, DISABLED, SHARDING_FLAG
    }
}
