package com.kingbbode.ehcache.monitor.ui.view;

import com.kingbbode.ehcache.monitor.ui.view.component.CacheDetailComponent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.navigator.SpringNavigator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;

/**
 * Created by YG-MAC on 2017. 12. 18..
 */
@SpringView(name = "detail")
public class CacheDetailView extends CacheDetailComponent {
    public CacheDetailView(CacheManager cacheManager, SpringNavigator navigator) {
        super(((EhCacheCacheManager) cacheManager).getCacheManager(), navigator);
    }
}
