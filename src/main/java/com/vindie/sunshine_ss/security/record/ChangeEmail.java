package com.vindie.sunshine_ss.security.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.springframework.util.StringUtils.hasLength;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangeEmail {
    private String newEmail;
    private Integer emailCode;

    public void validate() {
        var message = "ChangeEmail validate";
        assertTrue(hasLength(newEmail), message);
        assertTrue(emailCode != null, message);
    }
}
