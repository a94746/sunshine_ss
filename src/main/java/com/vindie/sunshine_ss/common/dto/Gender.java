package com.vindie.sunshine_ss.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gender {
    MALE((byte) 2),
    FEMALE((byte) 3),
    NON_BINARY((byte) 2);

    private final byte matchesNum;

}
