package com.vindie.sunshine_ss.security.record;

import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.springframework.util.StringUtils.hasLength;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    public String name;
    public String description;
    public Gender gender;
    public LocalDate bday;
    public Language lang;
    public Long locationId;

    public String email;
    public Integer emailCode;
    public String password;

    public String uniqueId;
    public String imei;
    public String wifiMac;
    public String phoneNum;
    public String appVersion;

    public void validate() {
        var message = "SignUpRequest validate";
        assertTrue(hasLength(name), message);
        assertTrue(gender != null, message);
        assertTrue(bday != null, message);
        assertTrue(lang != null, message);
        assertTrue(locationId != null, message);
        assertTrue(hasLength(email), message);
        assertTrue(emailCode != null, message);
        assertTrue(hasLength(password), message);
        assertTrue(hasLength(uniqueId), message);
    }
}
