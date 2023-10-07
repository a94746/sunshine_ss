package com.vindie.sunshine_ss.account.repo;

import com.vindie.sunshine_ss.account.dto.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepo extends JpaRepository<Contact, Long> {

    List<Contact> findAllByOwnerId(Long ownerId);

    Optional<Contact> findByIdAndOwnerId(Long id, Long ownerId);

    @Modifying
    @Query("DELETE FROM Contact c " +
            "WHERE c.id = :id " +
            "AND c.owner.id = :ownerId")
    void deleteByIdAndOwnerId(Long id, Long ownerId);

    @Query("SELECT COUNT(1) FROM Contact c " +
            "WHERE c.owner.id = :ownerId " +
            "AND c.key = :key")
    int countByOwnerIdAndKey(Long ownerId, String key);

    @Query("SELECT COUNT(1) FROM Contact c " +
            "WHERE c.owner.id = :ownerId")
    int countByOwnerId(Long ownerId);
}
