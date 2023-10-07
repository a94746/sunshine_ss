package com.vindie.sunshine_ss.common.record;

import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UiMyAccount {

    private String email;
    private String name;
    private String description;
    private Gender gender;
    private LocalDate bday;
    private Language lang;
    private Long locationId;
    private Double rating;
    private Byte actualMatchesNum;
    private Boolean prem;

    private UiMyFilter filter;

    private List<UiContact> contacts = new ArrayList<>();
    private List<UiPicInfo> picInfos = new ArrayList<>();
}
