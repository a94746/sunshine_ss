package com.vindie.sunshine_ss.timer.queue;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.common.event.ss.QueueElementsUpdateSsEvent;
import com.vindie.sunshine_ss.common.event.ss.SsEvent;
import com.vindie.sunshine_ss.common.timers.queue.QueueParserTimer;
import com.vindie.sunshine_ss.interfaces.WithDbData;
import com.vindie.sunshine_ss.queue.dto.EventLine;
import com.vindie.sunshine_ss.queue.dto.QueueElement;
import com.vindie.sunshine_ss.utils.DataUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueueParserTimerTest extends WithDbData {
    @Autowired
    private QueueParserTimer queueParserTimer;

    @Test
    void parse_event_line_for_one_test() {
        EventLine eventLine = DataUtils
                .newTypicalEventLine(account.getId(), null, true, false);
        eventLineRepo.save(eventLine);
        assertEquals(1, eventLineRepo.findAll().size());
        queueParserTimer.timer();
        List<EventLine> eventLines = eventLineRepo.findAll();
        assertEquals(1, eventLines.size());
        assertTrue(eventLines.get(0).getProcessed());
        List<QueueElement> queueElements = queueElementRepo.findAll();
        assertEquals(1, queueElements.size());
        assertEquals(eventLines.get(0).getId(), queueElements.get(0).getEventLine().getId());
        assertEquals(account.getId(), queueElements.get(0).getOwner().getId());

        checkEvent(List.of(account.getId()));
    }

    @Test
    void parse_event_line_for_location_without_notif_test() {
        EventLine eventLine = DataUtils
                .newTypicalEventLine(null, account.getLocation().getId(), false, true);
        eventLineRepo.save(eventLine);
        assertEquals(1, eventLineRepo.findAll().size());
        queueParserTimer.timer();
        List<EventLine> eventLines = eventLineRepo.findAll();
        assertEquals(1, eventLines.size());
        assertTrue(eventLines.get(0).getProcessed());
        List<QueueElement> queueElements = queueElementRepo.findAll();
        List<Long> peopleOnLocation = accountRepo.findAll()
                .stream()
                .filter(ac -> ac.getLocation().getId().equals(account.getLocation().getId()))
                .map(Account::getId)
                .toList();
        assertEquals(peopleOnLocation.size(), queueElements.size());
        queueElements.stream()
                .map(QueueElement::getOwner)
                .map(Account::getId)
                .forEach(id -> {
                    if (!peopleOnLocation.contains(id)) {
                        throw new RuntimeException();
                    }
                });
    }

    @Test
    void parse_event_line_for_location_with_notif_test() {
        EventLine eventLine = DataUtils
                .newTypicalEventLine(null, account.getLocation().getId(), true, false);
        eventLineRepo.save(eventLine);
        assertEquals(1, eventLineRepo.findAll().size());
        queueParserTimer.timer();
        List<EventLine> eventLines = eventLineRepo.findAll();
        assertEquals(1, eventLines.size());
        List<Long> peopleOnLocation = accountRepo.findAll()
                .stream()
                .filter(ac -> ac.getLocation().getId().equals(account.getLocation().getId()))
                .map(Account::getId)
                .toList();

        checkEvent(peopleOnLocation);
    }

    @Test
    void parse_event_line_error_cases() {
        eventLineRepo.save(DataUtils
                .newTypicalEventLine(account.getId(), account.getLocation().getId(), true, false));
        assertEquals(1, eventLineRepo.findAll().size());
        queueParserTimer.timer();
        assertEquals(0, eventLineRepo.findAll().size());
        assertEquals(0, queueElementRepo.findAll().size());

        eventLineRepo.save(DataUtils
                .newTypicalEventLine(null, null, true, false));
        assertEquals(1, eventLineRepo.findAll().size());
        queueParserTimer.timer();
        assertEquals(0, eventLineRepo.findAll().size());
        assertEquals(0, queueElementRepo.findAll().size());

        eventLineRepo.save(DataUtils
                .newTypicalEventLine(account.getId(), null, true, true));
        assertEquals(1, eventLineRepo.findAll().size());
        queueParserTimer.timer();
        assertEquals(0, eventLineRepo.findAll().size());
        assertEquals(0, queueElementRepo.findAll().size());

        eventLineRepo.save(DataUtils
                .newTypicalEventLine(null, account.getLocation().getId(), false, false));
        assertEquals(1, eventLineRepo.findAll().size());
        queueParserTimer.timer();
        assertEquals(0, eventLineRepo.findAll().size());
        assertEquals(0, queueElementRepo.findAll().size());
    }

    private void checkEvent(List<Long> ids) {
        checkEverySec().until(() -> eventEquals(SsEvent.Type.QUEUE_ELEMENTS_UPDATE));
        QueueElementsUpdateSsEvent event = (QueueElementsUpdateSsEvent) getEventAndClean();
        assertEquals(ids, event.getIds());
    }
}
