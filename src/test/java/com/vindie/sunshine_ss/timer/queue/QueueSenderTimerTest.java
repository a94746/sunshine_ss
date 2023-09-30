package com.vindie.sunshine_ss.timer.queue;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.base.WithDbData;
import com.vindie.sunshine_ss.common.event.ui.RealTimeSederUI;
import com.vindie.sunshine_ss.common.timers.queue.QueueParserTimer;
import com.vindie.sunshine_ss.common.timers.queue.QueueSenderTimer;
import com.vindie.sunshine_ss.queue.dto.EventLine;
import com.vindie.sunshine_ss.queue.dto.QueueElement;
import com.vindie.sunshine_ss.utils.DataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueSenderTimerTest extends WithDbData {
    @Autowired
    @InjectMocks
    public QueueSenderTimer queueSenderTimer;
    @Autowired
    private QueueParserTimer queueParserTimer;
    @MockBean
    private RealTimeSederUI realTimeSederUI;

    @Test
    void queue_sender_timer_test() {
        Account acc1 = accountRepo.save(DataUtils.newTypicalAccount(account.getLocation()));
        EventLine eventLine = DataUtils
                .newTypicalEventLine(null, acc1.getLocation().getId(), true, false);
        eventLineRepo.save(eventLine);
        queueParserTimer.timer();
        assertTrue(queueElementRepo.findAll()
                .stream()
                .map(QueueElement::getOwner)
                .map(Account::getId)
                .toList()
                .contains(acc1.getId()));
        List<QueueElement> queueElementsBefore2 = queueElementRepo.findAll();

        when(realTimeSederUI.send(anyMap()))
                .thenAnswer(answer -> ((Map<Long, List<QueueElement>>) answer.getArgument(0))
                        .get(acc1.getId()).stream().map(QueueElement::getId).toList());
        queueSenderTimer.timer();
        verify(realTimeSederUI).send(any());
        assertEquals(queueElementsBefore2.size() - 1, queueElementRepo.findAll().size());
    }
}
