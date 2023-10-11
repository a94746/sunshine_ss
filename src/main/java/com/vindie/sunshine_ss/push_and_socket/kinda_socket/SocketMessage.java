package com.vindie.sunshine_ss.push_and_socket.kinda_socket;

import com.vindie.sunshine_ss.common.record.event.ui.UiEvent;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SocketMessage {
    private UiEvent uiEvent;
    private Map<String, String> metadata = new HashMap<>();
}
