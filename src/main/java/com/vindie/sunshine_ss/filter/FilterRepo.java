package com.vindie.sunshine_ss.filter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterRepo extends JpaRepository<Filter, Long> {
}
