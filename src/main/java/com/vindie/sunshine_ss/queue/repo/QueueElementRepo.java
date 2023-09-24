package com.vindie.sunshine_ss.queue.repo;

import com.vindie.sunshine_ss.queue.dto.QueueElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueElementRepo extends JpaRepository<QueueElement, Long> {

    @Modifying
    @Query("DELETE FROM QueueElement qe " +
            "WHERE qe.created < :older")
    void deleteOlder(LocalDate older);

    @Modifying
    int deleteByIdIn(List<Long> ids);


    @Query("SELECT qe FROM QueueElement qe " +
            "LEFT JOIN qe.eventLine " +
            "LEFT JOIN qe.owner " +
            "WHERE qe.eventLine.notification = true")
    List<QueueElement> findAllNotifs();

    Optional<QueueElement> findFirstByEventLineId(Long eventId);
}
