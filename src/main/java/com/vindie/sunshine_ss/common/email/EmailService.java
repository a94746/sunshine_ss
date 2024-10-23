package com.vindie.sunshine_ss.common.email;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vindie.sunshine_ss.common.service.properties.PropertiesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
@Slf4j
@AllArgsConstructor
public class EmailService {
    private final Pattern emailAddressPattern = Pattern.compile("^(.{1,64})@(.{1,64})\\.(.{1,32})");
    private final Random random = new Random();
    private final PropertiesService properties;
    private EmailSenderService emailSenderService;

    private final CacheLoader<String, Integer> loader = new CacheLoader<>() {
        @Override
        public Integer load(String key) {
            return null;
        }
    };
    public final LoadingCache<String, Integer> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(100_000)
            .build(loader);

    public void sendEmailCode(String email) {
        int code = properties.isTestMode
                ? 123456
                : random.nextInt(0, 900000) + 100000;
        emailSenderService.sendEmail(email.trim(), "Sunshine confirmation code", "Code: " + code);
        cache.put(email.trim(), code);
    }

    public boolean isCorrectEmailCode(String email, int code) {
        try {
            Integer fromCache = cache.get(email.trim());
            return fromCache != null && fromCache.equals(code);
        } catch (ExecutionException e) {
            log.error("Error in checking email code", e);
            return false;
        }
    }

    public boolean isValid(String email) {
        return emailAddressPattern.matcher(email)
                .matches();
    }

}
