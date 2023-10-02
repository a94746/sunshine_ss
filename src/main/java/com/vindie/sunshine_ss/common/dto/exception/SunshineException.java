package com.vindie.sunshine_ss.common.dto.exception;

import com.vindie.sunshine_ss.common.dto.UiKey;
import lombok.Getter;

@Getter
public class SunshineException extends RuntimeException {
    private final UiKey uiExceprion;
    public SunshineException(UiKey uiExceprion) {
        super();
        this.uiExceprion = uiExceprion;
    }

}
