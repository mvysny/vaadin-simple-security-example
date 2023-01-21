package com.example.security.security;

import com.github.mvysny.vaadinsimplesecurity.AbstractLoginService;
import com.github.mvysny.vaadinsimplesecurity.SimpleUserWithRoles;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

/**
 * Loads {@link User}s from the database and stores them to the session. Call {@link #get()} to obtain the instance.
 */
public final class LoginService extends AbstractLoginService<User> {
    private LoginService() {
        // prevent accidental instantiation
    }

    /**
     * Logs in user with given username and password. Fails with {@link LoginException}
     * on failure.
     */
    public void login(@NotNull String username, @NotNull String password) throws LoginException {
        final User user = User.dao.findByUsername(username);
        if (user == null) {
            throw new FailedLoginException("Invalid username or password");
        }
        if (!user.passwordMatches(password)) {
            throw new FailedLoginException("Invalid username or password");
        }
        login(user);
    }

    @Override
    protected @NotNull SimpleUserWithRoles toUserWithRoles(@NotNull User user) {
        return new SimpleUserWithRoles(user.getUsername(), user.getRoleSet());
    }

    @NotNull
    public static LoginService get() {
        return get(LoginService.class, LoginService::new);
    }
}
