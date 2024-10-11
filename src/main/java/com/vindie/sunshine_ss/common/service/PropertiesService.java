package com.vindie.sunshine_ss.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.TimeZone;
import java.util.stream.IntStream;

import static io.micrometer.common.util.StringUtils.isBlank;

@Slf4j
@Service
public class PropertiesService {

    public final boolean isTestMode;
    public final int maxPicsPerAccount;
    public final int maxContactsPerAccount;
    public final Duration uiPicCacheTTL;
    public final String androidLeastVersion;
    public final String iosLeastVersion;
    public final Duration allowedFrequencyOfBdayChange;
    public final Duration locationLastChange;
    public final Duration matchesFrequency;
    public final String fromEmailAddress;
    public final Duration accountTTL;
    public final Duration matchTTL;
    public final Duration queueTTL;
    public final Duration actualMatchesTTL;
    public final Duration likedActualMatchesTTL;


    public PropertiesService(@Value("${app.is-test-mode}") String isTestMode,
                             @Value("${rules.max-pics-per-account}") Integer maxPicsPerAccount,
                             @Value("${rules.max-contacts-per-account}") Integer maxContactsPerAccount,
                             @Value("${rules.ui.ttl.pic-cache}") Duration uiPicCacheTTL,
                             @Value("${flow.least-version.android}") String androidLeastVersion,
                             @Value("${flow.least-version.ios}") String iosLeastVersion,
                             @Value("${rules.allowed-frequency-of-bday_change}") Duration allowedFrequencyOfBdayChange,
                             @Value("${rules.location_last_change}") Duration locationLastChange,
                             @Value("${rules.matches_frequency}") Duration matchesFrequency,
                             @Value("${email.service.send-from-address}") String fromEmailAddress,
                             @Value("${ttl.account}") Duration accountTTL,
                             @Value("${ttl.account}") Duration matchTTL,
                             @Value("${ttl.queue}") Duration queueTTL,
                             @Value("${rules.actual-matches-ttl}") Duration actualMatchesTTL,
                             @Value("${rules.liked-actual-matches-ttl}") Duration likedActualMatchesTTL) {
        if (!TimeZone.getTimeZone("UTC").toZoneId().equals(TimeZone.getDefault().toZoneId()))
            throw new IllegalStateException("TimeZone isn't UTC");

        this.isTestMode = "true".equals(isTestMode);
        if (this.isTestMode)
            IntStream.range(0, 5).forEach(ignore -> log.info("is-test-mode is on!!!"));

        validate(maxPicsPerAccount, "maxPicsPerAccount");
        this.maxPicsPerAccount = maxPicsPerAccount;

        validate(maxContactsPerAccount, "maxContactsPerAccount");
        this.maxContactsPerAccount = maxContactsPerAccount;

        validate(uiPicCacheTTL, "uiPicCacheTTL");
        this.uiPicCacheTTL = uiPicCacheTTL;

        validate(androidLeastVersion, "androidLeastVersion");
        this.androidLeastVersion = androidLeastVersion;

        validate(iosLeastVersion, "iosLeastVersion");
        this.iosLeastVersion = iosLeastVersion;

        validate(allowedFrequencyOfBdayChange, "allowedFrequencyOfBdayChange");
        this.allowedFrequencyOfBdayChange = allowedFrequencyOfBdayChange;

        validate(locationLastChange, "locationLastChange");
        this.locationLastChange = locationLastChange;

        validate(matchesFrequency, "matchesFrequency");
        this.matchesFrequency = matchesFrequency;

        validate(fromEmailAddress, "fromEmailAddress");
        this.fromEmailAddress = fromEmailAddress;

        validate(accountTTL, "accountTTL");
        this.accountTTL = accountTTL;

        validate(matchTTL, "matchTTL");
        this.matchTTL = matchTTL;

        validate(queueTTL, "queueTTL");
        this.queueTTL = queueTTL;

        validate(actualMatchesTTL, "actualMatchesTTL");
        this.actualMatchesTTL = actualMatchesTTL;

        validate(likedActualMatchesTTL, "likedActualMatchesTTL");
        this.likedActualMatchesTTL = likedActualMatchesTTL;
    }

    private void validate(Duration value, String name) {
        if (value == null || value.equals(Duration.ZERO))
            throw new IllegalStateException(name + " can't be null");
    }

    private void validate(String value, String name) {
        if (isBlank(value))
            throw new IllegalArgumentException(name + " can't be empty");
    }

    private void validate(Integer value, String name) {
        if (value == null || value.equals(0))
            throw new IllegalStateException(name + " can't be null");
    }
}
