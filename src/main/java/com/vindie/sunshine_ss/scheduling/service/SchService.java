package com.vindie.sunshine_ss.scheduling.service;

import com.vindie.sunshine_ss.location.Location;
import com.vindie.sunshine_ss.scheduling.dto.SchAccount;

import java.util.Collection;
import java.util.Map;

public interface SchService {

    public Map<Long, Map<Long, String>> calculate(Collection<SchAccount> accounts);

    public void runSch(Location location);
}
