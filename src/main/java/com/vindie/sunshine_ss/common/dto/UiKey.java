package com.vindie.sunshine_ss.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UiKey {
    NO_UNIQUE_EMAIL("<no_unique_email>"),
    NO_EXISTING_EMAIL("<no_existing_email>"),
    NOT_CORRECT_EMAIL_CODE("<not_correct_email_code>"),
    NOT_A_EMAIL("<not_a_email>");

    private final String key;
}
