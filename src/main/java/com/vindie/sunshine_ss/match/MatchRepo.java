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
            "LEFT JOIN FETCH m.partner.filter " +
            "LEFT JOIN FETCH m.partner.filter.relationsWithGenders " +
            "WHERE m.owner.id = :ownerId " +
            "AND m.date > :date")
    List<Match> findAllByOwnerIdAndDateAfter(Long ownerId, LocalDateTime date);

    @Query("SELECT m FROM Match m " +
            "LEFT JOIN FETCH m.owner " +
            "LEFT JOIN FETCH m.partner " +
            "WHERE pairId = :pairId ")
    List<Match> getPair(String pairId);

    @Query("SELECT m FROM Match m " +
            "LEFT JOIN FETCH m.owner " +
            "LEFT JOIN FETCH m.partner " +
            "LEFT JOIN FETCH m.partner.filter " +
            "LEFT JOIN FETCH m.partner.filter.relationsWithGenders " +
            "WHERE m.owner.id = :ownerId " +
            "AND m.date > :date " +
            "AND m.liked = TRUE")
    List<Match> findAllLikedByOwnerIdAndDateAfter(Long ownerId, LocalDateTime date);

    @Query("SELECT m FROM Match m " +
            "LEFT JOIN FETCH m.owner " +
            "LEFT JOIN FETCH m.partner " +
            "LEFT JOIN FETCH m.partner.filter " +
            "LEFT JOIN FETCH m.partner.filter.relationsWithGenders " +
            "WHERE m.partner.id = :partnerId " +
            "AND m.date > :date " +
            "AND m.liked = TRUE")
    List<Match> findAllLikedByPartnerIdAndDateAfter(Long partnerId, LocalDateTime date);

    @Query("SELECT m FROM Match m " +
            "WHERE m.pairId = :pairId " +
            "AND m.id != :thisId")
    Match getAnother(String pairId, Long thisId);
}
