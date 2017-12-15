package com.kingbbode.ehcache.monitor.ui.view;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import java.util.Arrays;
import java.util.stream.Collectors;

@SpringView(name = "table")
@SpringComponent
public class EhcacheTable extends CustomComponent implements View {

    @Autowired
    public EhcacheTable(org.springframework.cache.CacheManager springCacheManager) {
        CacheManager cacheManager = ((EhCacheCacheManager) springCacheManager).getCacheManager();
        VerticalLayout content = new VerticalLayout();
        setCompositionRoot(content);
        HorizontalLayout titleBar = new HorizontalLayout();
        titleBar.setWidth("100%");
        content.addComponent(titleBar);

        Label title = new Label("Kingbbode Ehcache Monitor");
        title.addStyleNames(ValoTheme.LABEL_BOLD, ValoTheme.LABEL_H1);
        titleBar.addComponent(title);
        titleBar.setExpandRatio(title, 1.0f);


        HorizontalLayout container = new HorizontalLayout();
        container.setWidth("100%");
        content.addComponent(container);

        Grid<Cache> grid = new Grid<>();
        grid.addColumn(Cache::getName).setCaption("Name");
        grid.addColumn(cache -> ((Double) (((double) cache.getStatistics().cacheHitCount()) / ((double) (cache.getStatistics().cacheMissCount() + cache.getStatistics().cacheHitCount())) * 100)).intValue() + "%").setCaption("Hit Ratio");
        grid.addColumn(cache -> cache.getCacheConfiguration().getMaxEntriesInCache()).setCaption("Max Size");
        grid.addColumn(Cache::getSize).setCaption("Size");
        grid.addColumn(Cache::getStatus).setCaption("Status");
        grid.addColumn(cache -> cache.getCacheConfiguration().getTimeToIdleSeconds()).setCaption("TTldle(s)");
        grid.addColumn(cache -> cache.getCacheConfiguration().getTimeToLiveSeconds()).setCaption("TTLive(s)");
        grid.addColumn(cache -> cache.getStatistics().cacheHitCount()).setCaption("hit");
        grid.addColumn(cache -> cache.getStatistics().cacheMissExpiredCount()).setCaption("miss : Expire");
        grid.addColumn(cache -> cache.getStatistics().cacheMissNotFoundCount()).setCaption("miss : Not Found");

        grid.setItems(Arrays.stream(cacheManager.getCacheNames())
                .map(cacheManager::getCache)
                .collect(Collectors.toList()));
        grid.setSizeFull();
        container.addComponent(grid);
    }
}
