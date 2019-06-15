package com.cloud.task.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 服务器维度简明信息对象.
 *
 */
@RequiredArgsConstructor
@Data
public final class ServerBriefInfo implements Serializable, Comparable<ServerBriefInfo> {

    private static final long serialVersionUID = 1133149706443681483L;

    private final String serverIp;

    private final Set<String> instances = new HashSet<>();

    private final Set<String> jobNames = new HashSet<>();

    private int instancesNum;

    private int jobsNum;

    private AtomicInteger disabledJobsNum = new AtomicInteger();

    @Override
    public int compareTo(final ServerBriefInfo o) {
        return (getServerIp()).compareTo(o.getServerIp());
    }
}
