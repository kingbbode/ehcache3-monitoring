package com.kingbbode.ehcache.monitor.data;

import net.sf.ehcache.Status;

public class CacheInfo {
    private String name;
    private String description;
    private Long hitRatio;
    private Integer elements;
    private Integer maxElements;
    private Status status;
    private Integer ttldle;
    private Integer ttlive;
    private Integer hit;
    private Integer missEx;
    private Integer missNot;
}
