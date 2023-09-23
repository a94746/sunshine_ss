package com.vindie.sunshine_ss.common.ss_event;

import lombok.Getter;

@Getter
public class CoupleLikedMatchSsEvent extends SsEvent {
    private final long firstLikedId;
    private final long secondLikedId; // тот кто лайкнул последним
    public CoupleLikedMatchSsEvent(long firstLikedId, long secondLikedId) {
        super(Type.COUPLE_LIKED_MATCH);
        this.firstLikedId = firstLikedId;
        this.secondLikedId = secondLikedId;
    }
}
