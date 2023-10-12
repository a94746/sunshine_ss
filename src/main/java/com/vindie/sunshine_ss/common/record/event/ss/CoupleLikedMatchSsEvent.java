package com.vindie.sunshine_ss.common.record.event.ss;

import lombok.Getter;

@Getter
public class CoupleLikedMatchSsEvent extends SsEvent {
    private final long firstLikedId;
    private final long secondLikedId; // who liked last
    public CoupleLikedMatchSsEvent(long firstLikedId, long secondLikedId) {
        super(Type.COUPLE_LIKED_MATCH);
        this.firstLikedId = firstLikedId;
        this.secondLikedId = secondLikedId;
    }
}
