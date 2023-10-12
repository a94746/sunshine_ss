package com.vindie.sunshine_ss.common.record.event.ss;

import lombok.Getter;

@Getter
public class SingleLikedMatchSsEvent extends SsEvent {
    private final long likingId; // who likes
    private final long likedId; // who was liked
    public SingleLikedMatchSsEvent(long likingId, long likedId) {
        super(Type.SINGLE_LIKED_MATCH);
        this.likingId = likingId;
        this.likedId = likedId;
    }
}