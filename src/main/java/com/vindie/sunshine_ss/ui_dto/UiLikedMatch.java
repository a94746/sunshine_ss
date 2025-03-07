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
public class UiLikedMatch {

    private Long id;
    private String name;
    private Integer age;
    private String description;
    private Gender gender;
    private String message;

    private Set<Relation> relations = new HashSet<>();
    private Set<ChatPref> chatPrefs = new HashSet<>();

    private List<UiPicInfo> picInfos = new ArrayList<>();
    private List<UiContact> contacts = new ArrayList<>();
}
