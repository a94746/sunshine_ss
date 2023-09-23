package com.vindie.sunshine_ss.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepo extends JpaRepository<Match, Long> {

    @Modifying
    @Query("DELETE FROM Match m " +
            "WHERE m.date < :older")
    void deleteOlder(LocalDateTime older);

    List<Match> findAllByOwnerId(Long ownerId);

    @Query("SELECT m FROM Match m " +
            "LEFT JOIN FETCH m.owner " +
            "LEFT JOIN FETCH m.partner " +
            "WHERE m.ownerId IN (:ownerIds)")
    List<Match> findAllByOwnerIds(List<Long> ownerIds);
}
