package com.vindie.sunshine_ss.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gender {
    MALE((byte) 2, (byte) 3),
    FEMALE((byte) 3, (byte) 4),
    NON_BINARY((byte) 2, (byte) 3);

    private final byte matchesNum;
    private final byte premMatchesNum;

}
