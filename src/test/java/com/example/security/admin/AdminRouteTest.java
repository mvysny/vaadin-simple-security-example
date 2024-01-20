package com.example.security.admin;

import com.example.security.AbstractAppTester;
import com.example.security.security.LoginRoute;
import com.example.security.user.UserRoute;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.NotFoundException;
import org.junit.jupiter.api.Test;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assertNone;
import static com.github.mvysny.kaributesting.v10.LocatorJ._assertOne;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AdminRouteTest extends AbstractAppTester {
    @Test
    public void loggedOutUserShouldNotBeAbleToSeeRoute() {
        UI.getCurrent().navigate(AdminRoute.class);
        _assertOne(LoginRoute.class);
    }

    @Test
    public void userShouldNotSeeRoute() {
        login("user");
        // the following exception is thrown for some reason
        // com.vaadin.flow.router.internal.ErrorStateRenderer$ExceptionsTrace: Exceptions handled by HasErrorParameter views are :com.vaadin.flow.router.NotFoundException, com.vaadin.flow.router.AccessDeniedException
        assertThrows(RuntimeException.class, () -> UI.getCurrent().navigate(AdminRoute.class));
        _assertNone(AdminRoute.class);
    }

    @Test
    public void adminShouldSeeRoute() {
        login("admin");
        UI.getCurrent().navigate(AdminRoute.class);
        _assertOne(AdminRoute.class);
    }
}
