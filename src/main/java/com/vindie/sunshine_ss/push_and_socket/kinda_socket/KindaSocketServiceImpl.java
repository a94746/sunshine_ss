package com.vindie.sunshine_ss.push_and_socket.kinda_socket;

import com.vindie.sunshine_ss.common.record.event.ui.UiEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
@AllArgsConstructor
public class KindaSocketServiceImpl implements KindaSocketService {
    @Override
    public void sendUpdate(UiEvent uiEvent, Collection<Long> ids) {

    }
}
