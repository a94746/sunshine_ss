package com.vindie.sunshine_ss.account.service;

import com.vindie.sunshine_ss.account.repo.DeviceRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DeviceService {

    @Autowired
    private DeviceRepo deviceRepo;

}
