package com.vindie.sunshine_ss.scheduling.service;

import com.vindie.sunshine_ss.scheduling.dto.SchAccount;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface SchService {

    public Map<Long, Set<Long>> calculate(Collection<SchAccount> accounts);

    public void runSch(Long locationId);
}
