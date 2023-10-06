package com.vindie.sunshine_ss.scheduling.dto;

import com.vindie.sunshine_ss.common.dto.ChatPref;
import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Relation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Getter
@EqualsAndHashCode
public class SchAccount {

    private final long id;
    private final int lastPresenceHours;
    private final int age;
    private final Gender gender;
    private final float rating;

    private final Collection<Long> avoidMatches;
    private final byte matchNum;
    private final boolean prem;

    private final Map<Gender, Set<Relation>> genders2relations;
    private final Set<ChatPref> chatPrefs;
    private final byte ageFrom;
    private final byte ageTo;

    @EqualsAndHashCode.Exclude
    private final Map<Long, String> resultMatches = new HashMap<>();

    public SchAccount(long id,
                      int lastPresenceHours,
                      int age,
                      Gender gender,
                      float rating,
                      Collection<Long> avoidMatches,
                      byte matchNum,
                      boolean prem,
                      Map<Gender, Set<Relation>> genders2relations,
                      Set<ChatPref> chatPrefs,
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
                ? EnumSet.noneOf(ChatPref.class)
                : EnumSet.copyOf(chatPrefs);
    }
}
