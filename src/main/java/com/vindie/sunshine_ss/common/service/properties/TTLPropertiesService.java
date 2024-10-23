package com.vindie.sunshine_ss.common.service.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.vindie.sunshine_ss.common.service.properties.PropertiesService.validate;

@Service
public class TTLPropertiesService {
    public final Duration uiPicCacheTTL;
    public final Duration accountTTL;
    public final Duration matchTTL;
    public final Duration queueTTL;
    public final Duration actualMatchesTTL;
    public final Duration likedActualMatchesTTL;
    public final Duration jwtTTL;

    public TTLPropertiesService(@Value("${rules.ui.ttl.pic-cache}") Duration uiPicCacheTTL,
                                @Value("${ttl.account}") Duration accountTTL,
                                @Value("${ttl.account}") Duration matchTTL,
                                @Value("${ttl.queue}") Duration queueTTL,
                                @Value("${rules.actual-matches-ttl}") Duration actualMatchesTTL,
                                @Value("${rules.liked-actual-matches-ttl}") Duration likedActualMatchesTTL,
                                @Value("${ttl.jwt}") Duration jwtTTL) {
        validate(uiPicCacheTTL, "uiPicCacheTTL");
        this.uiPicCacheTTL = uiPicCacheTTL;

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

        validate(jwtTTL, "jwtTTL");
        this.jwtTTL = jwtTTL;
    }
}
