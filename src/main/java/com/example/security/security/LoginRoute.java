package com.example.security.security;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.jetbrains.annotations.NotNull;

@Route("login")
@PageTitle("Login")
@AnonymousAllowed
public class LoginRoute extends VerticalLayout implements ComponentEventListener<AbstractLogin.LoginEvent> {

    private static final String LOGIN_SUCCESS_URL = "/";

    @NotNull
    private final LoginForm login = new LoginForm();

    public LoginRoute() {
        setSizeFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        login.addLoginListener(this);
        final LoginI18n loginI18n = new LoginI18n();
        loginI18n.setHeader(new LoginI18n.Header());
        loginI18n.getHeader().setTitle("Vaadin Simple Security Demo");
        loginI18n.setAdditionalInformation("Log in as user/user or admin/admin");
        login.setI18n(loginI18n);
        add(login);
    }

    @Override
    public void onComponentEvent(@NotNull AbstractLogin.LoginEvent loginEvent) {

        boolean authenticated = SecurityUtils.authenticate(
                loginEvent.getUsername(), loginEvent.getPassword());
        if (authenticated) {
            UI.getCurrent().getPage().setLocation(LOGIN_SUCCESS_URL);
        } else {
            login.setError(true);
        }
    }
}
