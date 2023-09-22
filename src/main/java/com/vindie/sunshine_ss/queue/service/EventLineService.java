package com.vindie.sunshine_ss.queue.service;

import com.vindie.sunshine_ss.queue.repo.EventLineRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class EventLineService {
    private EventLineRepo eventLineRepo;
}
