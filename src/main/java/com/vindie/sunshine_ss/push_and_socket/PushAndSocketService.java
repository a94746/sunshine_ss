package com.vindie.sunshine_ss.push_and_socket;

import com.vindie.sunshine_ss.common.dto.Language;
import com.vindie.sunshine_ss.common.record.event.ss.CoupleLikedMatchSsEvent;
import com.vindie.sunshine_ss.common.record.event.ss.DailyMatchesSsEvent;
import com.vindie.sunshine_ss.common.record.event.ss.QueueNotifsSsEvent;
import com.vindie.sunshine_ss.common.record.event.ss.SingleLikedMatchSsEvent;
import com.vindie.sunshine_ss.common.record.event.ui.UiEvent;
import com.vindie.sunshine_ss.common.service.LocaleService;
import com.vindie.sunshine_ss.push_and_socket.kinda_socket.KindaSocketService;
import com.vindie.sunshine_ss.push_and_socket.push.PushService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PushAndSocketService {
    private PushService pushService;
    private KindaSocketService kindaSocketService;
    private LocaleService localeService;

    @Async
    @EventListener
    public void coupleLikedMatchSsEventListener(CoupleLikedMatchSsEvent event) {
        final String pushTitle = localeService.localize("new.match.title", Language.EN);
        final String pushText = localeService.localize("new.match.text", Language.EN);
        pushService.sendPushs(pushTitle, pushText, List.of(event.getFirstLikedId()));
        kindaSocketService.sendUpdate(UiEvent.NEW_BOTH_LIKED_MATCH, List.of(event.getFirstLikedId()));
    }

    @Async
    @EventListener
    public void singleLikedMatchSsEventListener(SingleLikedMatchSsEvent event) {
        kindaSocketService.sendUpdate(UiEvent.YOU_WERE_LIKED, List.of(event.getLikedId()));
    }

    @Async
    @EventListener
    public void dailyMatchesSsEventListener(DailyMatchesSsEvent event) {
        final String pushTitle = localeService.localize("daily.match.title", Language.EN);
        final String pushText = localeService.localize("daily.match.text", Language.EN);
        pushService.sendPushs(pushTitle, pushText, event.getAccIds());
        kindaSocketService.sendUpdate(UiEvent.DAILY_MATCHES, event.getAccIds());
    }

    @Async
    @EventListener
    public void queueNotifsSsEventListener(QueueNotifsSsEvent event) {
        pushService.sendPushs(event.getNotifRecord().getTitle(), event.getNotifRecord().getText(),
                event.getNotifRecord().getIds());
    }
}
