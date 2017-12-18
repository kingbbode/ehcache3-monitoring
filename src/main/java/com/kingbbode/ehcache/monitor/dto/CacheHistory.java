package com.kingbbode.ehcache.monitor.dto;

import lombok.Getter;
import org.terracotta.statistics.archive.Timestamped;

/**
 * Created by YG-MAC on 2017. 12. 18..
 */
@Getter
public class CacheHistory implements Comparable<CacheHistory> {

    private Long value;
    private Long timestamp;

    public CacheHistory(Timestamped<Long> timestamped) {
        this.value = timestamped.getSample();
        this.timestamp = timestamped.getTimestamp();
    }

       /* CacheHistory merge(CacheHistory cacheHistory) {
            this.value += cacheHistory.value;
            return this;
        }*/

    @Override
    public int compareTo(CacheHistory o) {
        return Long.compare(this.timestamp, o.timestamp);
    }
}
