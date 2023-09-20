package com.vindie.sunshine_ss.pic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class PicService {

    @Autowired
    private PicRepo picRepo;

}
