package com.cloud.task.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 作业分片信息对象.
 *
 */
@Data
public final class ShardingInfo implements Serializable, Comparable<ShardingInfo> {

    private static final long serialVersionUID = 8587397581949456718L;

    private int item;

    private String serverIp;

    private String instanceId;

    private ShardingStatus status;

    private boolean failover;

    @Override
    public int compareTo(final ShardingInfo o) {
        return getItem() - o.getItem();
    }

    /**
     * 作业分片状态.
     *
     * @author caohao
     */
    public enum ShardingStatus {

        DISABLED, RUNNING, SHARDING_FLAG, PENDING;

        /**
         * 获取分片状态.
         * 
         * @param isDisabled 是否被禁用 
         * @param isRunning 是否在运行
         * @param isShardingFlag 是否需要分片
         * @return 作业运行时状态
         */
        public static ShardingStatus getShardingStatus(final boolean isDisabled, final boolean isRunning,
            final boolean isShardingFlag) {
            if (isDisabled) {
                return DISABLED;
            }
            if (isRunning) {
                return RUNNING;
            }
            if (isShardingFlag) {
                return SHARDING_FLAG;
            }
            return PENDING;
        }
    }
}
