package com.vindie.sunshine_ss.scheduling.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SchLockByLocation {
    private static Set<Long> usedKeys = ConcurrentHashMap.newKeySet();

    public static boolean tryLock(Long key) {
        return usedKeys.add(key);
    }

    public static void unlock(Long key) {
        usedKeys.remove(key);
    }

    public static boolean isLocked(Long key) {
        return usedKeys.contains(key);
    }
}
