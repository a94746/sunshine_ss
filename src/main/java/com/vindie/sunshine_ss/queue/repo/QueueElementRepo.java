package com.vindie.sunshine_ss.queue.repo;

import com.vindie.sunshine_ss.queue.dto.QueueElement;
import com.vindie.sunshine_ss.ui_dto.UiLoginOpeningDialog;
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

    @Query("SELECT new com.vindie.sunshine_ss.ui_dto.UiLoginOpeningDialog(qe.eventLine.title, qe.eventLine.text) " +
            "FROM QueueElement qe " +
            "WHERE qe.eventLine.openingDialog = TRUE " +
            "AND qe.owner.id = :ownerId")
    List<UiLoginOpeningDialog> findOpeningDialogsByOwner(Long ownerId);

    @Modifying
    @Query("DELETE FROM QueueElement qe " +
            "WHERE qe.eventLine.openingDialog = TRUE " +
            "AND qe.owner.id = :ownerId")
    void deleteOpeningDialogsByOwner(Long ownerId);

    Optional<QueueElement> findFirstByEventLineId(Long eventId);
}
