package com.example.security;

import com.example.security.security.LoginRoute;
import com.example.security.security.LoginService;
import com.github.mvysny.vaadinsimplesecurity.SimpleViewAccessChecker;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.jetbrains.annotations.NotNull;

public class ApplicationServiceInitListener implements VaadinServiceInitListener {
    // will also handle authorization
    private final SimpleViewAccessChecker accessChecker = SimpleViewAccessChecker.usingService(LoginService::get);
    
    public ApplicationServiceInitListener() {
        accessChecker.setLoginView(LoginRoute.class);
    }
    @Override
    public void serviceInit(@NotNull ServiceInitEvent event) {
        event.getSource().addUIInitListener(e -> e.getUI().addBeforeEnterListener(accessChecker));
    }
}
