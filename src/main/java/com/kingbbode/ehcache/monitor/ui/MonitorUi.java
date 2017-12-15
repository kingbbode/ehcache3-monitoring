package com.kingbbode.ehcache.monitor.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;

@Theme("valo")
@Title("Ehcache Monitoring")
@SpringUI
public class MonitorUi extends UI {
    private SpringViewProvider springViewProvider;

    private final CacheManager cacheManager;

    @Autowired
    public MonitorUi(org.springframework.cache.CacheManager cacheManager, SpringViewProvider springViewProvider) {
        this.cacheManager = ((EhCacheCacheManager) cacheManager).getCacheManager();
        this.springViewProvider = springViewProvider;
    }

    @Override
    protected void init(VaadinRequest request) {
        Navigator navigator = new Navigator(this, this);
        navigator.addProvider(this.springViewProvider);
        navigator.navigateTo("table");
    }
}
