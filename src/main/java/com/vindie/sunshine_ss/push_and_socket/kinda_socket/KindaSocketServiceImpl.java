package com.vindie.sunshine_ss.push_and_socket.kinda_socket;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vindie.sunshine_ss.common.record.event.ui.UiEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class KindaSocketServiceImpl implements KindaSocketService {
    private final ObjectMapper mapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
    private final SocketIOServer server;
    private final SocketIOBaseListeners socketIOBaseListeners;

    public KindaSocketServiceImpl(SocketIOServer socketIoServer, SocketIOBaseListeners socketIOBaseListeners) {
        socketIoServer.start();
        this.server = socketIoServer;
        this.server.addListeners(socketIOBaseListeners);
        this.socketIOBaseListeners = socketIOBaseListeners;
    }

    @Override
    public void sendUpdate(UiEvent uiEvent, Collection<Long> ids) {
        SocketMessage socketMessage = new SocketMessage();
        socketMessage.setUiEvent(uiEvent);
        String message;
        try {
            message = mapper.writeValueAsString(socketMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        for (Long id: ids) {
            Set<UUID> connections = socketIOBaseListeners.users.get(id);
            if (CollectionUtils.isEmpty(connections))
                continue;
            connections.forEach(c -> sendMessage(c, uiEvent, message));
        }
    }

    private void sendMessage(UUID client, UiEvent uiEvent, Object... args) {
        server.getClient(client).sendEvent(uiEvent.name(), args);
    }

    @Override
    public void destroy() {
        BroadcastOperations broadcastOperations = server.getBroadcastOperations();
        broadcastOperations.disconnect();
        server.stop();
    }

}
