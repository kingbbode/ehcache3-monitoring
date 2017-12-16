package com.kingbbode.ehcache.monitor.ui;

import com.kingbbode.ehcache.monitor.ui.view.component.DetailTable;
import com.kingbbode.ehcache.monitor.provider.DynamicViewProvider;
import com.kingbbode.ehcache.monitor.ui.layout.Menu;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import java.util.Arrays;

@Theme("valo")
@Title("Ehcache Monitoring")
@SpringUI
public class MonitorUi extends UI implements ViewChangeListener{

    private final CacheManager cacheManager;

    private final SpringNavigator springNavigator;

    private final DynamicViewProvider dynamicViewProvider;

    private Menu menu;

    @Autowired
    public MonitorUi(org.springframework.cache.CacheManager cacheManager, SpringNavigator springNavigator, DynamicViewProvider dynamicViewProvider) {
        this.cacheManager = ((EhCacheCacheManager) cacheManager).getCacheManager();
        this.springNavigator = springNavigator;
        this.dynamicViewProvider = dynamicViewProvider;
        this.springNavigator.addProvider(this.dynamicViewProvider);
    }

    @Override
    protected void init(VaadinRequest request) {
        addStyleName(ValoTheme.UI_WITH_MENU);

        HorizontalLayout layout = new HorizontalLayout();
        setContent(layout);

        layout.setStyleName("main-screen");

        CssLayout viewContainer = new CssLayout();
        viewContainer.addStyleName("valo-content");
        viewContainer.setSizeFull();

        this.springNavigator.init(this, viewContainer);
        this.springNavigator.addViewChangeListener(this);

        this.menu = new Menu(this.springNavigator);
        this.menu.addViewButton("", "전체", VaadinIcons.LIST);
        Arrays.stream(this.cacheManager.getCacheNames()).forEach(cacheName ->
        {
            this.menu.addViewButton(cacheName, cacheName, VaadinIcons.MAILBOX);
            this.dynamicViewProvider.add(cacheName, new DetailTable(this.cacheManager.getCache(cacheName), this.springNavigator));
        });


        layout.addComponent(this.menu);
        layout.addComponent(viewContainer);
        layout.setExpandRatio(viewContainer, 1);
        layout.setSizeFull();
        layout.setSpacing(false);
        this.springNavigator.navigateTo("");
    }

    @Override
    public boolean beforeViewChange(ViewChangeEvent event) {
        return true;
    }

    @Override
    public void afterViewChange(ViewChangeEvent event) {
        this.menu.setActiveView(event.getViewName());
    }
}
