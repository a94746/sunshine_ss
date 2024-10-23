package com.vindie.sunshine_ss.common.dto;


import com.vindie.sunshine_ss.scheduling.dto.SchRelation;

public enum Relation {
    LONG_LOVE,
    FAST_LOVE,
    FRIENDSHIP,
    ACQUAINTANCE;

    public SchRelation toSchRelation() {
        return switch (this) {
            case LONG_LOVE -> SchRelation.LONG_LOVE;
            case FAST_LOVE -> SchRelation.FAST_LOVE;
            case FRIENDSHIP -> SchRelation.FRIENDSHIP;
            case ACQUAINTANCE -> SchRelation.ACQUAINTANCE;
        };
    }
}
