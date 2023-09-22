package com.vindie.sunshine_ss.filter.repo;

import com.vindie.sunshine_ss.filter.dto.Filter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilterRepo extends JpaRepository<Filter, Long> {

    Optional<Filter> findByOwnerId(Long ownerId);
}
