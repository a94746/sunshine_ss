package com.vindie.sunshine_ss.common.record;

import com.vindie.sunshine_ss.common.dto.ChatPref;
import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Relation;
import lombok.Data;

import java.util.EnumMap;
import java.util.Map;

@Data
public class UiMyFilter {

    private Byte ageFrom;
    private Byte ageTo;

    private Map<Relation, Map<Gender, Boolean>> relationsWithGenders = new EnumMap<>(Relation.class);
    private Map<ChatPref, Boolean> chatPrefs = new EnumMap<>(ChatPref.class);
}
