package com.kingbbode.ehcache.monitor.component;

import com.kingbbode.ehcache.monitor.dto.CacheHistory;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by YG-MAC on 2017. 12. 18..
 */
@Component
public class CacheHistoryStore {
    private static DateTimeFormatter FORMATTER_FOR_GROUPING = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Map<String, Map<String, CacheHistory>> storage;
    private final CacheManager cacheManager;

    @Autowired
    public CacheHistoryStore(org.springframework.cache.CacheManager cacheManager) {
        this.storage = new HashMap<>();
        this.cacheManager = ((EhCacheCacheManager) cacheManager).getCacheManager();
    }

    @Scheduled(fixedDelay = 1000 * 40)
    public void fetch() {
        Arrays.stream(this.cacheManager.getCacheNames())
                .map(this.cacheManager::getCache)
                .forEach(this::saveCache);
    }

    private void saveCache(Cache cache) {
        cache.getStatistics().cacheHitOperation().count().history()
                .stream()
                .map(CacheHistory::new)
                .collect(Collectors.groupingBy(
                        o -> ofPatternForGrouping(o.getTimestamp())
                ))
                .values()
                .stream()
                .map(list -> list.stream().sorted(Comparator.reverseOrder()).findFirst().orElse(null))
                .filter(Objects::nonNull)
                .forEach(cacheHistory -> {
                    Map<String, CacheHistory> map = this.storage.getOrDefault(cache.getName(), new HashMap<>());
                    map.put(ofPatternForGrouping(cacheHistory.getTimestamp()), cacheHistory);
                    if(map.size()>3) {
                        String removeKey = map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue)).map(Map.Entry::getKey).findFirst().orElse("");
                        map.remove(removeKey);
                    }
                    this.storage.put(cache.getName(), map);
                });
    }

    private String ofPatternForGrouping(Long time) {
        return toLocalDateTime(time).format(FORMATTER_FOR_GROUPING);
    }

    private LocalDateTime toLocalDateTime(Long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    public List<CacheHistory> get(String name) {
        return this.storage.getOrDefault(name, Collections.emptyMap())
                .values().stream()
                .sorted(CacheHistory::compareTo)
                .collect(Collectors.toList());
    }
}
