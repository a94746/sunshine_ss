package com.vindie.sunshine_ss.push_and_socket.kinda_socket;

import com.vindie.sunshine_ss.common.record.event.ui.UiEvent;

import java.util.Collection;

public interface KindaSocketService {

    public void sendUpdate(UiEvent uiEvent, Collection<Long> ids);
}
