package com.vindie.sunshine_ss.security.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRecovery {
    public String email;
    public Integer emailCode;
    public String newPassword;
}
