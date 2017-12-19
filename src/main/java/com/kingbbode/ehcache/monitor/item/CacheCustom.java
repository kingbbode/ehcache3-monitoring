package com.kingbbode.ehcache.monitor.item;

import lombok.Builder;
import lombok.Getter;
import net.sf.ehcache.Status;

/**
 * Created by YG-MAC on 2017. 12. 19..
 */
@Getter
@Builder
public class CacheCustom {
    private String name;
    private Long maxSize;
    private Integer size;
    private Status status;
    private Long timeToIdleSeconds;
    private Long timeToLiveSeconds;
    private Long hitCount;
    private Long missExpiredCount;
    private Long missNotFoundCount;

    public Double getHitRatio() {
        return ((double) this.hitCount) / ((double) (this.hitCount + this.missExpiredCount + this.missNotFoundCount)) * 100;
    }

    private Long getMissCount() {
        return this.missExpiredCount + this.missNotFoundCount;
    }
}
