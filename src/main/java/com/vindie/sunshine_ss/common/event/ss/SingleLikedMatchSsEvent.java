package com.vindie.sunshine_ss.common.event.ss;

import lombok.Getter;

@Getter
public class SingleLikedMatchSsEvent extends SsEvent {
    private final long likingId; // тот кто лайкнул
    private final long likedId; // тот кого лайкнули
    public SingleLikedMatchSsEvent(long likingId, long likedId) {
        super(Type.SINGLE_LIKED_MATCH);
        this.likingId = likingId;
        this.likedId = likedId;
    }
}