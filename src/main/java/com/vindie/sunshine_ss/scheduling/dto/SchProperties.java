package com.vindie.sunshine_ss.scheduling.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@NoArgsConstructor
public class SchProperties {
    private float ratingBoundPrem;
    private float ratingBoundNotPrem;
    private Duration lastPresenceLimitPrem;
    private Duration lastPresenceLimitNotPrem;
}
