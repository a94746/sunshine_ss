package com.vindie.sunshine_ss.push_and_socket.push;

import java.util.Collection;

public interface PushService {
    public void sendPushs(String pushTitle, String pushText, Collection<Long> accIds);
}
