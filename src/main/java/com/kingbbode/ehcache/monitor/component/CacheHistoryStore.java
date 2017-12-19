package com.kingbbode.ehcache.monitor.component;

import com.kingbbode.ehcache.monitor.item.CacheCustom;
import com.kingbbode.ehcache.monitor.item.CacheCustomHistory;
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
    private final Map<String, CacheCustom> cacheStorage;
    private final Map<String, Map<String, CacheCustomHistory>> historyStorage;
    private final CacheManager cacheManager;

    @Autowired
    public CacheHistoryStore(org.springframework.cache.CacheManager cacheManager) {
        this.cacheStorage = new HashMap<>();
        this.historyStorage = new HashMap<>();
        this.cacheManager = ((EhCacheCacheManager) cacheManager).getCacheManager();
    }

    @Scheduled(fixedDelay = 1000 * 5)
    public void fetch() {
        Arrays.stream(this.cacheManager.getCacheNames())
                .map(this.cacheManager::getCache)
                .forEach(this::saveCache);
    }

    private void saveCache(Cache cache) {
        cache.getStatistics().cacheHitOperation().count().history()
                .stream()
                .map(CacheCustomHistory::new)
                .collect(Collectors.groupingBy(
                        o -> ofPatternForGrouping(o.getTimestamp())
                ))
                .values()
                .stream()
                .map(list -> list.stream().sorted(Comparator.reverseOrder()).findFirst().orElse(null))
                .filter(Objects::nonNull)
                .forEach(cacheHistory -> {
                    Map<String, CacheCustomHistory> map = this.historyStorage.getOrDefault(cache.getName(), new HashMap<>());
                    map.put(ofPatternForGrouping(cacheHistory.getTimestamp()), cacheHistory);
                    if(map.size()>3) {
                        String removeKey = map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue)).map(Map.Entry::getKey).findFirst().orElse("");
                        map.remove(removeKey);
                    }
                    this.historyStorage.put(cache.getName(), map);
                });
    }

    private void saveCache() {
        /*System.out.println("*********NAME : " + cache.getName());
        this.cacheManager.getCacheManagerPeerProviders().values()
                .stream()
                .map(cacheManagerPeerProvider -> cacheManagerPeerProvider.listRemoteCachePeers(cache))
                .flatMap(Collection::stream)
                .map(o -> (CachePeer)o)
                .map(cachePeer -> {
                    try {
                        return cachePeer.getElements(cachePeer.getKeys());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return Collections.emptyList();
                    }
                })
                .flatMap(Collection::stream)
                .forEach(System.out::println);
        System.out.println("*********NAME : " + cache.getName());*/
    }

    private String ofPatternForGrouping(Long time) {
        return toLocalDateTime(time).format(FORMATTER_FOR_GROUPING);
    }

    private LocalDateTime toLocalDateTime(Long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    public List<CacheCustomHistory> getHistory(String name) {
        return this.historyStorage.getOrDefault(name, Collections.emptyMap())
                .values().stream()
                .sorted(CacheCustomHistory::compareTo)
                .collect(Collectors.toList());
    }

    public List<CacheCustom> getAll(Collection<String> keys) {
        return keys.stream()
                .map(cacheManager::getCache)
                .map(cache ->
                        CacheCustom.builder()
                            .name(cache.getName())
                            .maxSize(cache.getCacheConfiguration().getMaxEntriesInCache())
                            .size(cache.getSize())
                            .status(cache.getStatus())
                            .timeToIdleSeconds(cache.getCacheConfiguration().getTimeToIdleSeconds())
                            .timeToLiveSeconds(cache.getCacheConfiguration().getTimeToLiveSeconds())
                            .hitCount(cache.getStatistics().cacheHitCount())
                            .missExpiredCount(cache.getStatistics().cacheMissExpiredCount())
                            .missNotFoundCount(cache.getStatistics().cacheMissExpiredCount())
                            .build()
                )
                .collect(Collectors.toList());
    }
}
