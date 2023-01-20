package com.example.security;

import com.example.security.security.LoginRoute;
import com.example.security.security.LoginService;
import com.example.security.welcome.WelcomeRoute;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.security.auth.login.LoginException;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assertNone;
import static com.github.mvysny.kaributesting.v10.LocatorJ._assertOne;

/**
 * Uses Karibu-Testing to test the app.
 */
public abstract class AbstractAppTester {
    @NotNull
    private static final Routes routes = new Routes().autoDiscoverViews("com.example.security");

    /**
     * Mocks the UI and logs in given user.
     */
    public static void login(@NotNull String username) {
        try {
            LoginService.get().login(username, username);
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
        // check that there is no LoginForm and everything is prepared
        _assertNone(LoginRoute.class);
        // in fact, by default the WelcomeView should be displayed
        _assertOne(WelcomeRoute.class);
    }

    @BeforeEach
    public void beforeEach() {
        MockVaadin.setup(routes);
    }

    @AfterEach
    public void afterEach() {
        MockVaadin.tearDown();
    }
}
