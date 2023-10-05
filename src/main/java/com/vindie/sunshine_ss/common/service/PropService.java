package com.vindie.sunshine_ss.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.TimeZone;

import static io.micrometer.common.util.StringUtils.isEmpty;

@Slf4j
@Service
public class PropService {

    public final boolean devNow;
    public final int maxPics;
    public final int maxContacts;
    public final Duration uiPicCacheTTL;
    public final String androidLeastVersion;
    public final String iosLeastVersion;


    public PropService(@Value("${app.dev-now}") String devNow,
                       @Value("${rules.max-pics}") Integer maxPics,
                       @Value("${rules.max-contacts}") Integer maxContacts,
                       @Value("${rules.ui.ttl.pic-cache}") Duration uiPicCacheTTL,
                       @Value("${flow.least-version.android}") String androidLeastVersion,
                       @Value("${flow.least-version.ios}") String iosLeastVersion) {
        if (!TimeZone.getTimeZone("UTC").toZoneId().equals(TimeZone.getDefault().toZoneId()))
            throw new IllegalStateException("TimeZone isn't UTC");

        this.devNow = "true".equals(devNow);
        if (this.devNow) {
            for (int i = 0; i < 5; i++) {
                log.warn("Dev-now is on!!!");
            }
        }

        if (maxPics == null || maxPics == 0)
            throw new IllegalStateException("maxPics can't be null");
        this.maxPics = maxPics;

        if (maxContacts == null || maxContacts == 0)
            throw new IllegalStateException("maxContacts can't be null");
        this.maxContacts = maxContacts;

        if (uiPicCacheTTL == null || uiPicCacheTTL.equals(Duration.ZERO))
            throw new IllegalStateException("uiPicCacheTTL can't be null");
        this.uiPicCacheTTL = uiPicCacheTTL;

        if (isEmpty(androidLeastVersion))
            throw new IllegalArgumentException("flow.least-version.android is empty");
        this.androidLeastVersion = androidLeastVersion;

        if (isEmpty(iosLeastVersion))
            throw new IllegalArgumentException("flow.least-version.ios is empty");
        this.iosLeastVersion = iosLeastVersion;
    }
}
