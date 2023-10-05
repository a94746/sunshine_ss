package com.vindie.sunshine_ss.common.service;

import com.vindie.sunshine_ss.common.record.UiSettings;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class CommonService {
    private final PropService propService;

    public UiSettings gerSettings() {
        var uiSettings = new UiSettings();
        uiSettings.setPicCacheTTL(propService.uiPicCacheTTL);
        return uiSettings;
    }
}
