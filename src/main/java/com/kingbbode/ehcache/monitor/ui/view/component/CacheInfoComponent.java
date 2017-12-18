package com.kingbbode.ehcache.monitor.ui.view.component;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import java.util.Arrays;
import java.util.stream.Collectors;

@SpringView(name = "")
public class CacheInfoComponent extends CustomComponent implements View {

    @Autowired
    public CacheInfoComponent(CacheManager cacheManager) {
        init(cacheManager);
    }

    private void init(CacheManager cacheManager) {
        VerticalLayout content = new VerticalLayout();
        content.addComponent(createTitleBar());
        content.addComponent(createContainer(cacheManager));
        setCompositionRoot(content);
    }

    private HorizontalLayout createTitleBar() {
        HorizontalLayout titleBar = new HorizontalLayout();
        titleBar.setWidth("100%");
        return titleBar;
    }

    private HorizontalLayout createContainer(CacheManager cacheManager) {
        HorizontalLayout container = new HorizontalLayout();
        container.setWidth("100%");
        container.addComponent(createCacheGrid(cacheManager));
        return container;
    }

    private Grid<Cache> createCacheGrid(CacheManager cacheManager) {
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
        return grid;
    }
}
