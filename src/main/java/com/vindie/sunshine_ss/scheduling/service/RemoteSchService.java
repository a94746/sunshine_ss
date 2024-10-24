package com.vindie.sunshine_ss.scheduling.service;


import com.vindie.sunshine_scheduler_dto.SchRequest;
import com.vindie.sunshine_scheduler_dto.SchResult;

public interface RemoteSchService {

    String startCalculation(SchRequest request);

    SchResult getCalculation(String uuid);
}
