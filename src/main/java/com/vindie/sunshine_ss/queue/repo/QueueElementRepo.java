package com.vindie.sunshine_ss.queue.repo;

import com.vindie.sunshine_ss.queue.dto.QueueElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface QueueElementRepo extends JpaRepository<QueueElement, Long> {

    @Modifying
    @Query("DELETE FROM QueueElement qe " +
            "WHERE qe.created < :older")
    void deleteOlder(LocalDate older);

    Optional<QueueElement> findFirstByEventLineId(Long eventId);
}
