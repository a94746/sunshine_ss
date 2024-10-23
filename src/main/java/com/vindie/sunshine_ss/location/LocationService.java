package com.vindie.sunshine_ss.location;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class LocationService {
    private LocationRepo locationRepo;

    public void save(Location location) {
        locationRepo.save(location);
    }

    public Location findById(Long id) {
        return locationRepo.findById(id).orElseThrow();
    }
}
