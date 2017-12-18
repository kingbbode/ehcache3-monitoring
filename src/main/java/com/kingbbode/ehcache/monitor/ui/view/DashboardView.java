package com.kingbbode.ehcache.monitor.ui.view;

import com.kingbbode.ehcache.monitor.ui.view.component.CacheInfoComponent;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;

@SpringView(name = "")
public class DashboardView extends CacheInfoComponent {
    @Autowired
    public DashboardView(CacheManager springCacheManager) {
        super(((EhCacheCacheManager) springCacheManager).getCacheManager());
    }
}
