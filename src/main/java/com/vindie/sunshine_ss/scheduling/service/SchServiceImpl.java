package com.vindie.sunshine_ss.scheduling.service;

import com.vindie.sunshine_scheduler_dto.SchRequest;
import com.vindie.sunshine_scheduler_dto.SchResult;
import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.account.service.AccountService;
import com.vindie.sunshine_ss.common.metrics.MetricService;
import com.vindie.sunshine_ss.common.record.event.ss.DailyMatchesSsEvent;
import com.vindie.sunshine_ss.location.Location;
import com.vindie.sunshine_ss.location.LocationService;
import com.vindie.sunshine_ss.match.Match;
import com.vindie.sunshine_ss.match.MatchService;
import com.vindie.sunshine_ss.scheduling.dto.ScheduleInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SchServiceImpl implements SchService {
    private AccountService accountService;
    private MatchService matchService;
    private LocationService locationService;
    private ApplicationEventPublisher eventPublisher;
    private MetricService metricService;
    private RemoteSchService remoteSchService;
    private SchParser schParser;

    private static Map<String, ScheduleInfo> startedSchedules = new ConcurrentHashMap();

    @Override
    @Async
    @Transactional
    public void runSch(Location location) {
        try {
            if (!SchLockByLocation.tryLock(location.getId())) {
                log.warn("Location {} is locked for sch", location.getName());
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            log.info("Start runSch for: {}. Time: {}", location.getName(), now);

            List<Account> accounts = accountService.findForScheduling(location.getId());
            Map<Account, Collection<Long>> accounts2avoidMatches = schParser.getAvoidMatches(accounts);

            SchRequest schRequest = schParser.getSchRequest(accounts2avoidMatches);
            String uuid = remoteSchService.startCalculation(schRequest);
            log.info("Started uuid = {}", uuid);
            startedSchedules.put(uuid, new ScheduleInfo(location.getId(), now));
        } catch (Exception e){
            SchLockByLocation.unlock(location.getId());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, ScheduleInfo> getStartedSchedules() {
        return startedSchedules;
    }

    @Transactional
    @Override
    public void saveSchResult(String uuid) {
        ScheduleInfo scheduleInfo = startedSchedules.get(uuid);
        if (scheduleInfo == null) {
            log.warn("Received deleted or unknown uuid = {}", uuid);
            return;
        }
        Location location = locationService.findById(scheduleInfo.getLocationId());
        try {
            location.setLastScheduling(LocalDateTime.now());
            locationService.save(location);

            SchResult schResult = remoteSchService.getCalculation(uuid);
            Collection<Match> matches = schParser.toMatches(schResult.getMatches());
            Set<Long> succeedAccountIds = schParser.getSucceedAccountIds(schResult.getMatches());

            matchService.saveAll(matches);
            accountService.incrementViews(succeedAccountIds);

            double durationSec = Duration.between(scheduleInfo.getStartTime(), Instant.now()).getNano() * 0.000000001D;
            log.info("End   runSch for: {} in {} sec", location.getName(), durationSec);

            saveMetrics(schResult, location.getId(), durationSec);
            sendDailyMatchesSsEvent(succeedAccountIds);
        } finally {
            SchLockByLocation.unlock(location.getId());
            startedSchedules.remove(uuid);
        }
    }

    @Override
    public void processErrorSchResult(String uuid) {
        ScheduleInfo scheduleInfo = startedSchedules.remove(uuid);
        if (scheduleInfo != null)
            SchLockByLocation.unlock(scheduleInfo.getLocationId());
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void timer() {
        var now = LocalDateTime.now();
        startedSchedules.entrySet().stream()
                .filter(e -> e.getValue().getLastUpdate().plusHours(1).isBefore(now))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet())
                .forEach(uuid -> {
                    log.warn("Deleted uuid = {}", uuid);
                    ScheduleInfo scheduleInfo = startedSchedules.remove(uuid);
                    if (scheduleInfo != null)
                        SchLockByLocation.unlock(scheduleInfo.getLocationId());
                });
    }

    private void sendDailyMatchesSsEvent(Set<Long> accountIds) {
        DailyMatchesSsEvent event = new DailyMatchesSsEvent(accountIds);
        eventPublisher.publishEvent(event);
    }

    private void saveMetrics(SchResult schResult, Long locationId, double durationSec) {
        int peopleNum = accountService.countByLocationId(locationId);
        metricService.setPeopleNum(locationId, peopleNum);
        metricService.setPeoplePercentHaveMatches(locationId,
                peopleNum == 0
                        ? 0
                        : ((schResult.getMatches().size() * 200D) / peopleNum));
        metricService.setNumOfMatchesPerPerson(locationId, schResult.getMetrics().getAvgMatches());
        metricService.setSchedulingTimeSec(locationId, durationSec);
    }

}
