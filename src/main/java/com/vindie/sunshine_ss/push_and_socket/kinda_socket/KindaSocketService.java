package com.vindie.sunshine_ss.push_and_socket.kinda_socket;

import com.vindie.sunshine_ss.common.record.event.ui.UiEvent;
import org.springframework.beans.factory.DisposableBean;

import java.util.Collection;

public interface KindaSocketService extends DisposableBean {

    public void sendUpdate(UiEvent uiEvent, Collection<Long> ids);
}
