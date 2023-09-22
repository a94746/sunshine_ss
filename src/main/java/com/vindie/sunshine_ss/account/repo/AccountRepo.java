package com.vindie.sunshine_ss.account.repo;

import com.vindie.sunshine_ss.account.dto.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {

    List<Account> findAllByLocationIdAndDeletedFalse(Long locationId);

    Optional<Account> findByIdAndDeletedFalse(Long id);

    @Query("SELECT a.id FROM Account a " +
            "WHERE a.lastPresence < :older")
    List<Long> findOlder(LocalDateTime older);

    @Modifying
    @Query("UPDATE Account a " +
            "SET a.premMatchesNum = NULL, " +
            "a.premMatchesTill = NULL " +
            "WHERE a.premMatchesTill < :now")
    void deleteOverduePrem(LocalDateTime now);

    @Query("UPDATE Account a " +
            "SET a.deleted = true " +
            "WHERE a.id = :id")
    void fakeDelete(Long id);
}
