package com.vindie.sunshine_ss.match;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class MatchService {

    @Autowired
    private MatchRepo matchRepo;

}
