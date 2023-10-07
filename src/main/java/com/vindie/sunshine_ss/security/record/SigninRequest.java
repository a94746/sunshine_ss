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
public class SigninRequest {
    public String email;
    public String pass;
    public String uniqueId;

    public void validate() {
        var message = "SigninRequest";
        assertTrue(hasLength(uniqueId), message);
    }
}
