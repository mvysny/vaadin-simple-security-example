package com.example.security;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;

import javax.annotation.security.PermitAll;

/**
 * The main layout. It uses the app-layout component which makes the app look like an Android Material app.
 */
@PermitAll
public class MainLayout extends AppLayout implements RouterLayout {
    public MainLayout() {
        addToNavbar(new DrawerToggle(), new H3("Vaadin Simple Security Demo"));
        addToDrawer(new VerticalLayout());
    }
}
