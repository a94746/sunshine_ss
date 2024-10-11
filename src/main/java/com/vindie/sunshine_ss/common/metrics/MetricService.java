package com.vindie.sunshine_ss.common.metrics;

import com.google.common.util.concurrent.AtomicDouble;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class MetricService {
    private static final String LOCATION = "location_id";
    private final MeterRegistry registry;
    private final Map<Long, AtomicDouble> schedulingTimeSec = new ConcurrentHashMap<>();
    private final Map<Long, AtomicInteger> peopleNum = new ConcurrentHashMap<>();
    private final Map<Long, AtomicDouble> peoplePercentHaveMatches = new ConcurrentHashMap<>();
    private final Map<Long, AtomicDouble> numOfMatchesPerPerson = new ConcurrentHashMap<>();

    public void setSchedulingTimeSec(Long locationId, Double value) {
        schedulingTimeSec.computeIfAbsent(locationId,
                k -> registry.gauge("scheduling.time.sec",
                List.of(Tag.of(LOCATION, String.valueOf(locationId))),
                new AtomicDouble(0)))
                .set(value);
    }

    public void setPeopleNum(Long locationId, Integer value) {
        peopleNum.computeIfAbsent(locationId,
                k -> registry.gauge("people.num",
                        List.of(Tag.of(LOCATION, String.valueOf(locationId))),
                        new AtomicInteger(0)))
                .set(value);
    }

    public void setPeoplePercentHaveMatches(Long locationId, Double value) {
        peoplePercentHaveMatches.computeIfAbsent(locationId,
                k -> registry.gauge("people.percent.have.matches",
                        List.of(Tag.of(LOCATION, String.valueOf(locationId))),
                        new AtomicDouble(0)))
                .set(value);
    }

    public void setNumOfMatchesPerPerson(Long locationId, Double value) {
        numOfMatchesPerPerson.computeIfAbsent(locationId,
                k -> registry.gauge("matches.num.per.person",
                        List.of(Tag.of(LOCATION, String.valueOf(locationId))),
                        new AtomicDouble(0)))
                .set(value);
    }
}
