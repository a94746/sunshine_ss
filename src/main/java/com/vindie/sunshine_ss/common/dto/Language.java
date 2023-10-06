package com.vindie.sunshine_ss.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language {
    EN("en"),
    RU("ru");

    private final String key;
}
