package com.vindie.sunshine_ss.timer.queue;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.base.WithData;
import com.vindie.sunshine_ss.common.timers.queue.QueueCleanerTimer;
import com.vindie.sunshine_ss.common.timers.queue.QueueParserTimer;
import com.vindie.sunshine_ss.location.Location;
import com.vindie.sunshine_ss.queue.dto.EventLine;
import com.vindie.sunshine_ss.queue.dto.QueueElement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueueCleanerTimerTest extends WithData {
    @Autowired
    private QueueCleanerTimer queueCleanerTimer;
    @Autowired
    private QueueParserTimer queueParserTimer;

    @Test
    void queue_cleaner_timer_test() {
        Location location = locationRepo.save(dataUtils.newTypicalLocation());
        Account acc1 = accountRepo.save(dataUtils.newTypicalAccount(location, false));
        Account acc2 = accountRepo.save(dataUtils.newTypicalAccount(location, false));
        EventLine eventLine1 = dataUtils
                .newTypicalEventLine(acc1.getId(), null, false, true);
        EventLine eventLine2 = dataUtils
                .newTypicalEventLine(acc2.getId(), null, false, true);
        eventLineRepo.saveAll(List.of(eventLine1, eventLine2));
        queueParserTimer.timer();
        List<QueueElement> queueElements = queueElementRepo.findAll();
        assertEquals(2, queueElements.size());
        queueElements.get(0).setCreated(LocalDate.now().minusDays(QueueCleanerTimer.TTL_DAYS + 1));
        queueElementRepo.save(queueElements.get(0));

        queueCleanerTimer.timer();
        assertEquals(1, queueElementRepo.findAll().size());
        assertEquals(1, eventLineRepo.findAll().size());
        queueElements.get(1).setCreated(LocalDate.now().minusDays(QueueCleanerTimer.TTL_DAYS + 1));
        queueElementRepo.save(queueElements.get(1));

        queueCleanerTimer.timer();
        assertEquals(0, queueElementRepo.findAll().size());
        assertEquals(0, eventLineRepo.findAll().size());
        assertTrue(accountRepo.findById(acc1.getId()).isPresent());
        assertTrue(accountRepo.findById(acc2.getId()).isPresent());
    }
}
