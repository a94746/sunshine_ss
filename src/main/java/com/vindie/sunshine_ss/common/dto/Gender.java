package com.vindie.sunshine_ss.common.dto;

import com.vindie.sunshine_ss.scheduling.dto.SchGender;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Gender {
    MALE,
    FEMALE,
    NON_BINARY;

    public SchGender toSchGender() {
        return switch (this) {
            case MALE -> SchGender.MALE;
            case FEMALE -> SchGender.FEMALE;
            case NON_BINARY -> SchGender.NON_BINARY;
        };
    }

}
