package com.vindie.sunshine_ss.common.service.properties;

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

    public final TTLPropertiesService ttl;
    public final GenderMatchNumPropertiesService genderMatchNum;
    public final boolean isTestMode;
    public final int maxPicsPerAccount;
    public final int maxContactsPerAccount;
    public final String androidLeastVersion;
    public final String iosLeastVersion;
    public final Duration allowedFrequencyOfBdayChange;
    public final Duration locationLastChange;
    public final Duration matchesFrequency;
    public final String fromEmailAddress;
    public final String jwtSigningKey;
    public final Duration lastPresenceLimit;
    public final Duration lastPresenceLimitPrem;
    public final float ratingBound;
    public final float ratingBoundPrem;


    public PropertiesService(GenderMatchNumPropertiesService genderMatchNumProperties,
                             TTLPropertiesService ttlProperties,
                             @Value("${app.is-test-mode}") String isTestMode,
                             @Value("${rules.max-pics-per-account}") Integer maxPicsPerAccount,
                             @Value("${rules.max-contacts-per-account}") Integer maxContactsPerAccount,
                             @Value("${flow.least-version.android}") String androidLeastVersion,
                             @Value("${flow.least-version.ios}") String iosLeastVersion,
                             @Value("${rules.allowed-frequency-of-bday_change}") Duration allowedFrequencyOfBdayChange,
                             @Value("${rules.location_last_change}") Duration locationLastChange,
                             @Value("${rules.matches_frequency}") Duration matchesFrequency,
                             @Value("${email.service.send-from-address}") String fromEmailAddress,
                             @Value("${token.signing.key}") String jwtSigningKey,
                             @Value("${rules.last-presence-limit}") Duration lastPresenceLimit,
                             @Value("${rules.last-presence-limit-prem}") Duration lastPresenceLimitPrem,
                             @Value("${rules.rating-bound}") Float ratingBound,
                             @Value("${rules.rating-bound-prem}") Float ratingBoundPrem) {
        if (!TimeZone.getTimeZone("UTC").toZoneId().equals(TimeZone.getDefault().toZoneId()))
            throw new IllegalStateException("TimeZone isn't UTC");

        this.ttl = ttlProperties;
        this.genderMatchNum = genderMatchNumProperties;

        this.isTestMode = "true".equals(isTestMode);
        if (this.isTestMode)
            IntStream.range(0, 5).forEach(ignore -> log.info("is-test-mode is on!!!"));

        validate(maxPicsPerAccount, "maxPicsPerAccount");
        this.maxPicsPerAccount = maxPicsPerAccount;

        validate(maxContactsPerAccount, "maxContactsPerAccount");
        this.maxContactsPerAccount = maxContactsPerAccount;

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

        validate(jwtSigningKey, "jwtSigningKey");
        this.jwtSigningKey = jwtSigningKey;

        validate(lastPresenceLimit, "lastPresenceLimit");
        this.lastPresenceLimit = lastPresenceLimit;

        validate(lastPresenceLimitPrem, "lastPresenceLimitPrem");
        this.lastPresenceLimitPrem = lastPresenceLimitPrem;

        validate(ratingBoundPrem, "ratingBoundPrem");
        this.ratingBoundPrem = ratingBoundPrem;

        validate(ratingBound, "ratingBound");
        this.ratingBound = ratingBound;
    }

    public static void validate(Duration value, String name) {
        if (value == null || value.equals(Duration.ZERO))
            throw new IllegalStateException(name + " can't be null");
    }

    public static void validate(String value, String name) {
        if (isBlank(value))
            throw new IllegalArgumentException(name + " can't be empty");
    }

    public static void validate(Integer value, String name) {
        if (value == null || value.equals(0))
            throw new IllegalStateException(name + " can't be null");
    }

    public static void validate(Byte value, String name) {
        if (value == null || value.intValue() == 0)
            throw new IllegalStateException(name + " can't be null");
    }

    public static void validate(Float value, String name) {
        if (value == null || value.equals(0f))
            throw new IllegalStateException(name + " can't be null");
    }
}
