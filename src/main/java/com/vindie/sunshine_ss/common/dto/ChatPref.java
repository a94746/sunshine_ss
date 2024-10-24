package com.vindie.sunshine_ss.common.dto;

import com.vindie.sunshine_scheduler_dto.SchChatPref;

public enum ChatPref {
    LIFE,
    ONLINE;

    public SchChatPref toSchChatPref() {
        return switch (this) {
            case LIFE -> SchChatPref.LIFE;
            case ONLINE -> SchChatPref.ONLINE;
        };
    }
}
