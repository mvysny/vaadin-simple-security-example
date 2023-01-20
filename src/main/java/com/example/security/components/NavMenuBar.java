package com.example.security.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Taken from the <a href="https://vaadin.com/docs/latest/components/app-layout">App Layout</a> component documentation.
 */
public class NavMenuBar extends Tabs implements AfterNavigationObserver {
    /**
     * Maps route class to its tab.
     */
    @NotNull
    private final Map<Class<?>, Tab> tabs = new HashMap<>();

    public NavMenuBar() {
        setOrientation(Orientation.VERTICAL);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        final Class<? extends HasElement> currentRoute = event.getActiveChain().get(0).getClass();
        setSelectedTab(tabs.get(currentRoute));
    }

    public void addRoute(@NotNull VaadinIcon icon, @NotNull Class<? extends Component> routeClass) {
        addRoute(icon, routeClass, getRouteTitle(routeClass));
    }

    public void addRoute(@NotNull VaadinIcon icon, @NotNull Class<? extends Component> routeClass, @NotNull String title) {
        final RouterLink routerLink = new RouterLink(routeClass);
        addNavIcon(routerLink, icon);
        routerLink.add(new Span(title));
        final Tab tab = new Tab(routerLink);
        add(tab);
        tabs.put(routeClass, tab);
    }

    public void addButton(@NotNull VaadinIcon icon, @NotNull String title, @NotNull SerializableRunnable clickListener) {
        final Div div = new Div();
        addNavIcon(div, icon);
        div.add(new Span(title));
        div.addClickListener(e -> clickListener.run());
        add(new Tab(div));
    }

    private void addNavIcon(@NotNull HasComponents parent, @NotNull VaadinIcon icon) {
        final Icon i = icon.create();
        parent.add(i);
        i.getStyle().set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("margin-inline-start", "var(--lumo-space-xs)")
                .set("padding", "var(--lumo-space-xs)");
    }

    @NotNull
    public static String getRouteTitle(@NotNull Class<?> routeClass) {
        final PageTitle title = routeClass.getAnnotation(PageTitle.class);
        return title == null ? routeClass.getSimpleName() : title.value();
    }
}
