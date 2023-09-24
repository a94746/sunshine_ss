package com.vindie.sunshine_ss.common.timers.queue;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.common.event.ss.QueueElementsUpdateSsEvent;
import com.vindie.sunshine_ss.location.Location;
import com.vindie.sunshine_ss.location.LocationRepo;
import com.vindie.sunshine_ss.queue.dto.EventLine;
import com.vindie.sunshine_ss.queue.dto.QueueElement;
import com.vindie.sunshine_ss.queue.repo.EventLineRepo;
import com.vindie.sunshine_ss.queue.repo.QueueElementRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.lang.Boolean.FALSE;
import static java.util.Collections.singletonList;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * тут мы переносим внесённые вручную записи в таблицу eventLine в таблицу Queue
 */
@Service
@AllArgsConstructor
@Slf4j
public class QueueParserTimer {
    public static final int INTERVAL_MIN = 10;

    private EventLineRepo eventLineRepo;
    private AccountRepo accountRepo;
    private LocationRepo locationRepo;
    private QueueElementRepo queueElementRepo;
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    @Scheduled(fixedRate = INTERVAL_MIN, timeUnit = TimeUnit.MINUTES)
    public void timer() {
        List<EventLine> eventLines = eventLineRepo.findAll();
        if (isEmpty(eventLines)) return;
        eventLines.stream()
                .filter(ev -> FALSE.equals(ev.getProcessed()))
                .forEach(this::process);
    }

    private void process(EventLine ev) {
        if ((ev.getNotification() && ev.getOpeningDialog())
                || (!ev.getNotification() && !ev.getOpeningDialog())
                || (ev.getOwnerId() == null && ev.getLocationId() == null)
                || (ev.getOwnerId() != null && ev.getLocationId() != null)) {
            eventLineRepo.delete(ev);
            return;
        }

        if (ev.getOwnerId() != null) {
            Optional<Account> optAccount = accountRepo.findByIdAndDeletedFalse(ev.getOwnerId());
            if (optAccount.isPresent()) {
                save(ev, singletonList(optAccount.get()));
            } else {
                eventLineRepo.delete(ev);
                return;
            }
        }
        if (ev.getLocationId() != null) {
            Optional<Location> optLocation = locationRepo.findById(ev.getLocationId());
            if (optLocation.isPresent()) {
                List<Account> accs = accountRepo.findAllByLocationIdAndDeletedFalse(optLocation.get().getId());
                save(ev, accs);
            } else {
                eventLineRepo.delete(ev);
                return;
            }
        }
        ev.setProcessed(true);
        eventLineRepo.save(ev);
    }

    private void save(EventLine eventLine, List<Account> accs) {
        List<Long> accIds = accs.stream()
                .map(Account::getId)
                .toList();
        List<QueueElement> queueElements = accs.stream()
                .map(acc -> {
                    QueueElement qe = new QueueElement();
                    qe.setOwner(acc);
                    qe.setEventLine(eventLine);
                    return qe;
                })
                .toList();
        queueElementRepo.saveAll(queueElements);

        if (Boolean.TRUE.equals(eventLine.getNotification())) {
            QueueElementsUpdateSsEvent event = new QueueElementsUpdateSsEvent(accIds);
            eventPublisher.publishEvent(event);
        }
    }
}
