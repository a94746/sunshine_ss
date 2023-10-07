package com.vindie.sunshine_ss.security.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.springframework.util.StringUtils.hasLength;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRecovery {
    public String email;
    public Integer emailCode;
    public String newPassword;

    public void validate() {
        var message = "PasswordRecovery validate";
        assertTrue(hasLength(email), message);
        assertTrue(emailCode != null, message);
        assertTrue(hasLength(newPassword), message);
    }
}
