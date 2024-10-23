package com.vindie.sunshine_ss.scheduling.service;

import com.vindie.sunshine_ss.location.Location;
import com.vindie.sunshine_ss.scheduling.dto.ScheduleInfo;

import java.util.Map;

public interface SchService {

    void runSch(Location location);

    Map<String, ScheduleInfo> getStartedSchedules();

    void saveSchResult(String uuid);

    void processErrorSchResult(String uuid);
}
