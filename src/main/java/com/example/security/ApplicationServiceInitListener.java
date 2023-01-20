package com.example.security;

import com.example.security.security.LoginRoute;
import com.github.mvysny.vaadinsimplesecurity.SimpleViewAccessChecker;
import com.github.mvysny.vaadinsimplesecurity.inmemory.InMemoryLoginService;
import com.github.mvysny.vaadinsimplesecurity.inmemory.InMemoryUser;
import com.github.mvysny.vaadinsimplesecurity.inmemory.InMemoryUserRegistry;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ApplicationServiceInitListener implements VaadinServiceInitListener {
    // will also handle authorization
    private final SimpleViewAccessChecker accessChecker = SimpleViewAccessChecker.usingService(InMemoryLoginService::get);
    
    public ApplicationServiceInitListener() {
        // let's create the users
        InMemoryUserRegistry.get().registerUser(new InMemoryUser("user", "user", Set.of("ROLE_USER")));
        InMemoryUserRegistry.get().registerUser(new InMemoryUser("admin", "admin", Set.of("ROLE_USER", "ROLE_ADMIN")));
        accessChecker.setLoginView(LoginRoute.class);
    }
    @Override
    public void serviceInit(@NotNull ServiceInitEvent event) {
        event.getSource().addUIInitListener(e -> e.getUI().addBeforeEnterListener(accessChecker));
    }
}
