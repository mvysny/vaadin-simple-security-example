package com.example.security;

import com.example.security.security.LoginRoute;
import com.example.security.security.LoginService;
import com.github.mvysny.vaadinsimplesecurity.SimpleNavigationAccessControl;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.jetbrains.annotations.NotNull;

public class ApplicationServiceInitListener implements VaadinServiceInitListener {
    // will also handle authorization
    private final SimpleNavigationAccessControl accessControl = SimpleNavigationAccessControl.usingService(LoginService::get);
    
    public ApplicationServiceInitListener() {
        accessControl.setLoginView(LoginRoute.class);
    }
    @Override
    public void serviceInit(@NotNull ServiceInitEvent event) {
        event.getSource().addUIInitListener(e -> e.getUI().addBeforeEnterListener(accessControl));
    }
}
