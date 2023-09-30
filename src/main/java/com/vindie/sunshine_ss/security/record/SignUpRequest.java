package com.vindie.sunshine_ss.security.record;

import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Language;
import com.vindie.sunshine_ss.location.Location;
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
    public Location location;

    public String email;
    public String password;

    public String uniqueId;
    public String imei;
    public String wifiMac;
    public String phoneNum;
    public String appVersion;
}
