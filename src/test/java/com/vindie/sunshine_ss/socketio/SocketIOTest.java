package com.vindie.sunshine_ss.socketio;

import com.vindie.sunshine_ss.base.WithMvcAndSocket;
import com.vindie.sunshine_ss.common.record.event.ss.CoupleLikedMatchSsEvent;
import com.vindie.sunshine_ss.common.record.event.ss.DailyMatchesSsEvent;
import com.vindie.sunshine_ss.common.record.event.ss.SingleLikedMatchSsEvent;
import com.vindie.sunshine_ss.common.record.event.ui.UiEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SocketIOTest extends WithMvcAndSocket {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Test
    void test() throws Exception {
        var event1 = new SingleLikedMatchSsEvent(3, account.getId());
        CompletableFuture<Boolean> future1 = new CompletableFuture<>();
        socket.on(UiEvent.YOU_WERE_LIKED.name(), args -> future1.complete(true));

        var event2 = new CoupleLikedMatchSsEvent(account.getId(), 3);
        CompletableFuture<Boolean> future2 = new CompletableFuture<>();
        socket.on(UiEvent.NEW_BOTH_LIKED_MATCH.name(), args -> future2.complete(true));

        var event3 = new DailyMatchesSsEvent(List.of(account.getId()));
        CompletableFuture<Boolean> future3 = new CompletableFuture<>();
        socket.on(UiEvent.DAILY_MATCHES.name(), args -> future3.complete(true));

        socket.connect();
        checkEverySec().timeout(10, TimeUnit.SECONDS)
                .until(() -> socket.connected());

        eventPublisher.publishEvent(event1);
        assertTrue(future1.get(10, TimeUnit.SECONDS));
        eventPublisher.publishEvent(event2);
        assertTrue(future2.get(10, TimeUnit.SECONDS));
        eventPublisher.publishEvent(event3);
        assertTrue(future3.get(10, TimeUnit.SECONDS));
    }
}
