package com.example.security.security;

import com.github.mvysny.vaadinsimplesecurity.inmemory.InMemoryLoginService;

import java.io.Serializable;

public class LoginService implements Serializable {
    public static InMemoryLoginService get() {
        return InMemoryLoginService.get();
    }
}
