package com.kingbbode.ehcache.monitor.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.distribution.CachePeer;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum RemoteUtils {
    ;

    public static Map<Object, Long> listRemoteElementHitCount(CacheManager cacheManager, Cache ehcache) {
        Map<Object, Long> map = new HashMap<>();
        cacheManager.getCacheManagerPeerProviders().values()
                .stream()
                .map(cacheManagerPeerProvider -> cacheManagerPeerProvider.listRemoteCachePeers(ehcache))
                .flatMap(Collection::stream)
                .map(o -> (CachePeer) o)
                .map(cachePeer -> {
                    try {
                        return cachePeer.getElements(cachePeer.getKeys());
                    } catch (RemoteException e) {
                        return Collections.emptyList();
                    }
                })
                .flatMap(Collection::stream)
                .forEach(o -> {
                    Long before = map.getOrDefault(((Element) o).getObjectKey(), 0L);
                    map.put(((Element) o).getObjectKey(), before + ((Element) o).getHitCount());
                });
        return map;
    }

    public static long sumRemoteElementHitCount(CacheManager cacheManager, Cache ehcache) {
        return cacheManager.getCacheManagerPeerProviders().values()
                .stream()
                .map(cacheManagerPeerProvider -> cacheManagerPeerProvider.listRemoteCachePeers(ehcache))
                .flatMap(Collection::stream)
                .map(o -> (CachePeer)o)
                .map(cachePeer -> {
                    try {
                        return cachePeer.getElements(cachePeer.getKeys());
                    } catch (RemoteException e) {
                        return Collections.emptyList();
                    }
                })
                .flatMap(Collection::stream)
                .mapToLong(o -> ((Element)o).getHitCount())
                .sum();
    }
}
