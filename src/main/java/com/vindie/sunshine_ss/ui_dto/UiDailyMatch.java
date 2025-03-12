package com.vindie.sunshine_ss.ui_dto;

import com.vindie.sunshine_ss.common.dto.ChatPref;
import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Relation;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class UiDailyMatch {

    private Long id;
    private String pairId;
    private String name;
    private Integer age;
    private String description;
    private Gender gender;
    private Boolean likedYou;
    private Boolean likedByYou;
    private String message;

    private Set<Relation> relations = new HashSet<>();
    private Set<ChatPref> chatPrefs = new HashSet<>();

    private List<UiPicInfo> picInfos = new ArrayList<>();
}
