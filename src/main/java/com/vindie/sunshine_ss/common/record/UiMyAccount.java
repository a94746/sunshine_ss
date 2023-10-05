package com.vindie.sunshine_ss.common.record;

import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Language;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class UiMyAccount {

    private String email;
    private String name;
    private String description;
    private Gender gender;
    private LocalDate bday;
    private Language lang;
    private String locationName;
    private Double rating;
    private Byte actualMatchesNum;
    private Boolean prem;

    private UiMyFilter filter;

    private Map<String, String> contacts = new HashMap<>();
    private List<UiPicInfo> picInfos = new ArrayList<>();
}
