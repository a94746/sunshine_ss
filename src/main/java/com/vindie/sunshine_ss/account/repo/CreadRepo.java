package com.vindie.sunshine_ss.account.repo;

import com.vindie.sunshine_ss.account.dto.Cread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreadRepo extends JpaRepository<Cread, Long> {
    Optional<Cread> findFirstByEmail(String email);

    @Modifying
    @Query("UPDATE Cread c " +
            "SET c.email = :newEmail " +
            "WHERE c.owner.id = :ownerId")
    public void changeEmail(String newEmail, Long ownerId);
}
