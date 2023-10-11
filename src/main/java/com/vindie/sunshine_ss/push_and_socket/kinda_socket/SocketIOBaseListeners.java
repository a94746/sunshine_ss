package com.vindie.sunshine_ss.push_and_socket.kinda_socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.vindie.sunshine_ss.push_and_socket.kinda_socket.conf.SocketIOAuth;
import com.vindie.sunshine_ss.security.record.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
public class SocketIOBaseListeners {
    public final Map<Long, Set<UUID>> users = new ConcurrentHashMap<>();
    private final SocketIOAuth socketIOAuth;

    @OnConnect
    public void onConnect(SocketIOClient client) {
        User user = socketIOAuth.getUser(client.getHandshakeData().getHttpHeaders());
        if (user != null) {
            users.computeIfAbsent(user.getId(),
                    ignore -> ConcurrentHashMap.newKeySet()).add(client.getSessionId());
        }
    }

    @OnDisconnect
    public synchronized void onDisconnect(SocketIOClient client) {
        Long id = null;
        int size = 0;
        for (Map.Entry<Long, Set<UUID>> entry : users.entrySet()) {
            if (entry.getValue().contains(client.getSessionId())) {
                id = entry.getKey();
                size = entry.getValue().size();
            }
        }
        if (id != null) {
            if (size <= 1) {
                users.remove(id);
            } else {
                var uuids = users.get(id);
                if (!CollectionUtils.isEmpty(uuids))
                    uuids.remove(client.getSessionId());
            }
        }
    }
}
