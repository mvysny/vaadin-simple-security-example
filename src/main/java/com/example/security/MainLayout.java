package com.example.security;

import com.example.security.admin.AdminRoute;
import com.example.security.components.NavMenuBar;
import com.example.security.user.UserRoute;
import com.example.security.welcome.WelcomeRoute;
import com.github.mvysny.vaadinsimplesecurity.inmemory.InMemoryLoginService;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.RouterLayout;
import org.jetbrains.annotations.NotNull;

/**
 * The main layout. It uses the app-layout component which makes the app look like an Android Material app.
 */
public class MainLayout extends AppLayout implements RouterLayout {
    @NotNull
    private final Div contentPane = new Div();

    public MainLayout() {
        addToNavbar(new DrawerToggle(), new H3("Vaadin Simple Security Demo"));
        final NavMenuBar navMenuBar = new NavMenuBar();
        navMenuBar.addRoute(VaadinIcon.NEWSPAPER, WelcomeRoute.class);
        navMenuBar.addRoute(VaadinIcon.LIST, UserRoute.class);
        navMenuBar.addRoute(VaadinIcon.COG, AdminRoute.class);
        navMenuBar.addButton(VaadinIcon.SIGN_OUT, "Log Out", () -> InMemoryLoginService.get().logout());
        addToDrawer(navMenuBar);

        setContent(contentPane);
        contentPane.setSizeFull();
        contentPane.addClassName("app-content");
    }

    @Override
    public void showRouterLayoutContent(@NotNull HasElement content) {
        contentPane.getElement().appendChild(content.getElement());
    }
}
