package com.kingbbode.ehcache.monitor.item;

import lombok.Getter;
import org.terracotta.statistics.archive.Timestamped;

/**
 * Created by YG-MAC on 2017. 12. 18..
 */
@Getter
public class CacheCustomHistory implements Comparable<CacheCustomHistory> {

    private Long value;
    private Long timestamp;

    public CacheCustomHistory(Timestamped<Long> timestamped) {
        this.value = timestamped.getSample();
        this.timestamp = timestamped.getTimestamp();
    }

       /* CacheHistory merge(CacheHistory cacheHistory) {
            this.value += cacheHistory.value;
            return this;
        }*/

    @Override
    public int compareTo(CacheCustomHistory o) {
        return Long.compare(this.timestamp, o.timestamp);
    }
}
