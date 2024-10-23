package com.vindie.sunshine_ss.scheduling.service;

import com.vindie.sunshine_ss.scheduling.dto.SchRequest;
import com.vindie.sunshine_ss.scheduling.dto.SchResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RemoteSchServiceImpl implements RemoteSchService {

    @Override
    public String startCalculation(SchRequest request) {
        return null;
    }

    @Override
    public SchResult getCalculation(String uuid) {
        return null;
    }
}
