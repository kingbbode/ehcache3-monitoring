package com.kingbbode.ehcache.monitor.ui.view.component;

import com.kingbbode.ehcache.monitor.component.CacheHistoryStore;
import com.kingbbode.ehcache.monitor.item.CacheCustom;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringView(name = "")
public class CacheInfoComponent extends CustomComponent implements View {

    private final CacheHistoryStore store;

    @Autowired
    public CacheInfoComponent(CacheManager cacheManager, CacheHistoryStore store) {
        this.store = store;
        init(cacheManager);
    }

    private void init(CacheManager cacheManager) {
        VerticalLayout content = new VerticalLayout();
        content.addComponent(createTitleBar());
        content.addComponent(createCacheGrid(cacheManager));
        content.addComponent(createCacheCharts(cacheManager));
        setCompositionRoot(content);
    }

    private VerticalLayout createCacheCharts(CacheManager cacheManager) {

        VerticalLayout verticalLayout = new VerticalLayout();
        List<HorizontalLayout> horizontalLayoutList = IntStream.range(0, cacheManager.getCacheNames().length)
                .mapToObj(value -> new HorizontalLayout())
                .collect(Collectors.toList());
        String[] names = cacheManager.getCacheNames();
        IntStream.range(0, names.length).forEach(value -> horizontalLayoutList.get(value / 3).addComponent(new CacheLineChart(names[value], this.store.getHistory(names[value]))));
        horizontalLayoutList.forEach(components -> {
            verticalLayout.addComponent(components);
            components.setSizeFull();
        });
        verticalLayout.setSizeFull();
        return verticalLayout;
    }

    private HorizontalLayout createTitleBar() {
        HorizontalLayout titleBar = new HorizontalLayout();
        Label title = new Label("EHCACHE LIST");
        titleBar.addComponent(title);
        titleBar.setExpandRatio(title, 1.0f);
        title.addStyleNames(ValoTheme.LABEL_H1, ValoTheme.LABEL_BOLD, ValoTheme.LABEL_COLORED);

        return titleBar;
    }

    private Grid<CacheCustom> createCacheGrid(CacheManager cacheManager) {
        List<CacheCustom> items = this.store.getAll(Arrays.asList(cacheManager.getCacheNames()));
        Grid<CacheCustom> grid = new Grid<>();
        grid.addColumn(CacheCustom::getName).setCaption("Name");
        grid.addColumn(cache -> cache.getHitRatio() + "%").setCaption("Hit Ratio");
        grid.addColumn(CacheCustom::getMaxSize).setCaption("Max Size");
        grid.addColumn(CacheCustom::getSize).setCaption("Size");
        grid.addColumn(CacheCustom::getStatus).setCaption("Status");
        grid.addColumn(CacheCustom::getTimeToIdleSeconds).setCaption("TTldle(s)");
        grid.addColumn(CacheCustom::getTimeToLiveSeconds).setCaption("TTLive(s)");
        grid.addColumn(CacheCustom::getHitCount).setCaption("hit");
        grid.addColumn(CacheCustom::getMissExpiredCount).setCaption("miss : Expire");
        grid.addColumn(CacheCustom::getMissNotFoundCount).setCaption("miss : Not Found");
        grid.setItems(items);
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        int cacheSize = items.size();
        if (cacheSize != 0) {
            grid.setHeightByRows(cacheSize > 10 ? 10 : cacheSize);
        }
        return grid;
    }
}
