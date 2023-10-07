package com.vindie.sunshine_ss.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UiKey {
    NO_UNIQUE_EMAIL("<no_unique_email>"),
    NO_UNIQUE_CONTACT_KEY("<no_unique_contact_key>"),
    CONTACTS_MAXIMUM("<contacts_maximum>"),
    PICS_MAXIMUM("<pics_maximum>"),
    NO_EXISTING_EMAIL("<no_existing_email>"),
    NOT_CORRECT_EMAIL_CODE("<not_correct_email_code>"),
    MATCH_NOT_AT_ACTUAL("<match_not_at_actual>"),
    CANT_CHANGE_BDAY_SO_OFTEN("<cant_change_bday_so_often>"),
    CANT_CHANGE_LOCATION_SO_OFTEN("<cant_change_location_so_often>"),
    NOT_A_EMAIL("<not_a_email>");

    private final String key;
}
