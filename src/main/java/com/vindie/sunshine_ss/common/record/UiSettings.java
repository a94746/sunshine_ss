package com.vindie.sunshine_ss.common.record;

import lombok.Data;

import java.time.Duration;

@Data
public class UiSettings {

    private Duration picCacheTTL;
    private Duration matchesFrequency;
}
