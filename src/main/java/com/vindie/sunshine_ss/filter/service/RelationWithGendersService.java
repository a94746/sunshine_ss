package com.vindie.sunshine_ss.filter.service;

import com.vindie.sunshine_ss.filter.repo.RelationWithGendersRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RelationWithGendersService {
    private RelationWithGendersRepo relationWithGendersRepo;

}
