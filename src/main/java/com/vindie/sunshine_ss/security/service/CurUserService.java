package com.vindie.sunshine_ss.security.service;

import com.vindie.sunshine_ss.security.record.User;
import org.springframework.util.Assert;

import java.util.Optional;

public class CurUserService {
    private CurUserService() {}

    private static final ThreadLocal<User> USER = new InheritableThreadLocal<>();

    public static User get() {
        return Optional.ofNullable(USER.get())
                .orElseThrow();
    }

    public static void set(User user) {
        Assert.notNull(user, "User can't be null");
        USER.set(user);
    }

    public static void clear() {
        USER.remove();
    }
}
