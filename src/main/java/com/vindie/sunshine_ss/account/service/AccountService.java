package com.vindie.sunshine_ss.account.service;

import com.vindie.sunshine_ss.account.repo.AccountRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {

    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private ContactService contactService;
    @Autowired
    private CreadService creadService;
    @Autowired
    private DeviceService deviceService;

}
