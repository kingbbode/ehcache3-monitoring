package com.kingbbode.ehcache.monitor.ui.view.component;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ComponentRenderer;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 * Created by YG-MAC on 2017. 12. 16..
 */
public class CacheDetailComponent extends CustomComponent implements View {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private CacheManager cacheManager;
    private SpringNavigator navigator;

    public CacheDetailComponent(CacheManager cacheManager, SpringNavigator navigator) {
        this.cacheManager = cacheManager;
        this.navigator = navigator;
    }

    protected void init(String name) {
        Cache ehcache = this.cacheManager.getCache(name);
        VerticalLayout content = new VerticalLayout();
        content.addComponent(createTopButtons(ehcache));
        content.addComponent(createContainer(ehcache));
        setCompositionRoot(content);
    }

    private VerticalLayout createContainer(Cache ehcache) {
        VerticalLayout container = new VerticalLayout();
        container.setWidth("100%");
        container.addComponent(createCacheInfoGrid(ehcache));
        container.addComponent(createDetailGrid(ehcache));
        return container;
    }

    private HorizontalLayout createTopButtons(Cache ehcache) {
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidth("100%");
        topBar.addComponent(new Button("Refresh", (Button.ClickListener) event -> init(ehcache.getName())));
        topBar.addComponent(new Button("Flush", (Button.ClickListener) event -> {
            ehcache.flush();
            init(ehcache.getName());
        }));
        return topBar;
    }

    private Grid<Cache> createCacheInfoGrid(Cache ehcache) {
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
        grid.setItems(Collections.singletonList(ehcache));
        grid.setWidth("100%");
        return grid;
    }

    private Grid<Element> createDetailGrid(Cache ehcache) {
        Grid<Element> elements = new Grid<>();
        elements.addColumn(Element::getObjectKey).setCaption("Name");
        elements.addColumn(Element::getObjectValue).setCaption("Value");
        elements.addColumn(element -> toPattern(element.getCreationTime())).setCaption("Create Time");
        elements.addColumn(element -> toPattern(element.getLastAccessTime())).setCaption("Access Time");
        elements.addColumn(element -> toPattern(element.getLastUpdateTime())).setCaption("Update Time");
        elements.addColumn(Element::getVersion).setCaption("Version");
        elements.addColumn(Element::getHitCount).setCaption("Hit");
        elements.addColumn(element -> new Button("remove", (Button.ClickListener) event -> {
            ehcache.remove(element.getObjectKey());
            navigator.navigateTo(ehcache.getName());
        }), new ComponentRenderer()).setCaption("");

        elements.setItems(ehcache.getAll(ehcache.getKeys()).values());
        elements.setWidth("100%");
        return elements;
    }

    private String toPattern(Long time) {
        return toLocalDateTime(time).format(formatter);
    }

    private LocalDateTime toLocalDateTime(Long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }



    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        String cacheName = event.getParameterMap().getOrDefault("cache", "");
        if("".equals(cacheName)) {
            event.getNavigator().navigateTo(cacheName);
            return;
        }
        init(cacheName);
    }
}
