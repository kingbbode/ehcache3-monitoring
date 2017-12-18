package com.kingbbode.ehcache.monitor.ui.view.component;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
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

    public CacheDetailComponent(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    private void init(String name) {
        Cache ehcache = this.cacheManager.getCache(name);
        VerticalLayout content = new VerticalLayout();
        content.addComponent(createTitleBar());
        content.addComponent(createCacheInfoGrid(ehcache));
        content.addComponent(createControlButtons(ehcache));
        content.addComponent(createDetailGrid(ehcache));
        setSizeFull();
        setCompositionRoot(content);
    }

    private HorizontalLayout createTitleBar() {
        HorizontalLayout titleBar = new HorizontalLayout();
        Label title = new Label("EHCACHE DETAIL");
        titleBar.addComponent(title);
        titleBar.setExpandRatio(title, 1.0f); // Expand
        title.addStyleNames(ValoTheme.LABEL_H1, ValoTheme.LABEL_BOLD, ValoTheme.LABEL_COLORED);
        return titleBar;
    }

    private HorizontalLayout createControlButtons(Cache ehcache) {
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.addComponent(new Button("Refresh", (Button.ClickListener) event -> init(ehcache.getName())));
        topBar.addComponent(new Button("Flush", (Button.ClickListener) event -> {
            ehcache.flush();
            init(ehcache.getName());
        }));
        topBar.setWidth("30%");
        //topBar.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
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
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setHeightByRows(1);
        return grid;
    }

    private Grid<Element> createDetailGrid(Cache ehcache) {
        Grid<Element> grid = new Grid<>();
        grid.addColumn(Element::getObjectKey).setCaption("Name");
        grid.addColumn(Element::getObjectValue).setCaption("Value");
        grid.addColumn(element -> toPattern(element.getCreationTime())).setCaption("Create Time");
        grid.addColumn(element -> toPattern(element.getLastAccessTime())).setCaption("Access Time");
        grid.addColumn(element -> toPattern(element.getLastUpdateTime())).setCaption("Update Time");
        grid.addColumn(Element::getVersion).setCaption("Version");
        grid.addColumn(Element::getHitCount).setCaption("Hit");
        grid.addColumn(element -> {
            Button button = new Button(VaadinIcons.TRASH);
            button.addClickListener(event -> {
                ehcache.remove(element.getObjectKey());
                init(ehcache.getName());
            });
            return button;
            }, new ComponentRenderer()).setCaption("");

        grid.setItems(ehcache.getAll(ehcache.getKeys()).values());
        grid.setWidth("100%");
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        int ehcacheSize = ehcache.getKeys().size();
        if(ehcacheSize != 0) {
            grid.setHeightByRows(ehcacheSize > 10 ? 10 : ehcacheSize);
        }
        return grid;
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
