package com.vindie.sunshine_ss.security.record;

import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    public String locationName;

    public String email;
    public Integer emailCode;
    public String password;

    public String uniqueId;
    public String imei;
    public String wifiMac;
    public String phoneNum;
    public String appVersion;
}
