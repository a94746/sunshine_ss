package com.vindie.sunshine_ss.account.service;

import com.vindie.sunshine_ss.account.repo.ContactRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ContactService {
    private ContactRepo contactRepo;

}