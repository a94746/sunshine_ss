package com.vindie.sunshine_ss.scheduling.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class SchAccount {

    private long id;
    private int lastPresenceHours;
    private int age;
    private SchGender gender;
    private float rating;

    private Collection<Long> avoidMatches;
    private byte matchNum;
    private boolean prem;

    private Map<SchGender, Set<SchRelation>> genders2relations;
    private Set<SchChatPref> chatPrefs;
    private byte ageFrom;
    private byte ageTo;

    @EqualsAndHashCode.Exclude
    private final Set<Long> resultPartners = new HashSet<>();

    public SchAccount(long id,
                      int lastPresenceHours,
                      int age,
                      SchGender gender,
                      float rating,
                      Collection<Long> avoidMatches,
                      byte matchNum,
                      boolean prem,
                      Map<SchGender, Set<SchRelation>> genders2relations,
                      Set<SchChatPref> chatPrefs,
                      byte ageFrom,
                      byte ageTo) {
        this.id = id;
        this.lastPresenceHours = lastPresenceHours;
        this.age = age;
        this.gender = gender;
        this.rating = rating;
        this.matchNum = matchNum;
        this.prem = prem;
        this.ageFrom = ageFrom;
        this.ageTo = ageTo;

        this.avoidMatches = CollectionUtils.isEmpty(avoidMatches)
                ? Collections.emptyList()
                : new HashSet<>(avoidMatches);

        this.genders2relations = CollectionUtils.isEmpty(genders2relations)
                ? Collections.emptyMap()
                : new EnumMap<>(genders2relations);

        this.chatPrefs = CollectionUtils.isEmpty(chatPrefs)
                ? EnumSet.noneOf(SchChatPref.class)
                : EnumSet.copyOf(chatPrefs);
    }
}
