package com.vindie.sunshine_ss.push_and_socket.push;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
@AllArgsConstructor
public class PushServiceImpl implements PushService {
    @Override
    public void sendPushs(String pushTitle, String pushText, Collection<Long> accIds) {

    }
}
