package com.vindie.sunshine_ss.ui_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.springframework.util.StringUtils.hasLength;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UiContact {

    private Long id;
    private String key;
    private String value;

    public void validate() {
        var message = "UiContact validate";
        assertTrue(hasLength(key), message);
        assertTrue(id != null, message);
        assertTrue(hasLength(value), message);
    }

    public void validate2() {
        var message = "UiContact validate2";
        assertTrue(hasLength(key), message);
        assertTrue(hasLength(value), message);
    }
}
