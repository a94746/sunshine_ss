package com.vindie.sunshine_ss.queue.repo;

import com.vindie.sunshine_ss.queue.dto.EventLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventLineRepo extends JpaRepository<EventLine, Long> {
}
