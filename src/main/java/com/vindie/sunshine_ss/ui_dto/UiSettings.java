package com.vindie.sunshine_ss.ui_dto;

import lombok.Data;

import java.time.Duration;

@Data
public class UiSettings {

    private Duration picCacheTTL;
    private Duration matchesFrequency;
}
