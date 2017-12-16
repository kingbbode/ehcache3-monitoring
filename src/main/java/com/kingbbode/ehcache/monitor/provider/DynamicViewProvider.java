package com.kingbbode.ehcache.monitor.provider;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by YG-MAC on 2017. 12. 16..
 */
@Component
public class DynamicViewProvider implements ViewProvider{
    private Map<String, View> viewMap;

    public DynamicViewProvider() {
        this.viewMap = new HashMap<>();
    }

    public void add(String name, View view) {
        this.viewMap.put(name, view);
    }

    @Override
    public String getViewName(String viewAndParameters) {
        return this.viewMap.containsKey(viewAndParameters)?viewAndParameters:null;
    }

    @Override
    public View getView(String viewName) {
        return this.viewMap.getOrDefault(viewName, null);
    }
}
