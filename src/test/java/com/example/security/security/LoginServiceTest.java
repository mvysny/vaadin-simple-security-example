package com.example.security.security;

import com.example.security.AbstractAppTester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LoginServiceTest extends AbstractAppTester {
    @Test
    public void successfulLogin() throws Exception {
        LoginService.get().login("user", "user");
        assertNotNull(LoginService.get().getCurrentUser());
        assertEquals("user", LoginService.get().getCurrentUser().getUsername());
    }
}
