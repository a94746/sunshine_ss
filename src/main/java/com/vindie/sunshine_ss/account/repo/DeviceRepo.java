package com.vindie.sunshine_ss.account.repo;

import com.vindie.sunshine_ss.account.dto.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepo extends JpaRepository<Device, Long> {
}
