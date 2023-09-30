package com.vindie.sunshine_ss.account.repo;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.security.record.User;
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
            "a.premTill = NULL " +
            "WHERE a.premTill < :now")
    void deleteOverduePrem(LocalDateTime now);

    @Modifying
    @Query("UPDATE Account a " +
            "SET a.deleted = true " +
            "WHERE a.id = :id")
    void fakeDelete(Long id);

    @Query("SELECT a FROM Account a " +
            "LEFT JOIN a.matchesOwner " +
            "LEFT JOIN a.matchesOwner.partner " +
            "LEFT JOIN a.filter " +
            "LEFT JOIN a.filter.relationsWithGenders " +
            "LEFT JOIN a.filter.chatPrefs " +
            "LEFT JOIN a.filter.relationsWithGenders.genders " +
            "WHERE a.location.id = :locationId " +
            "AND a.filter IS NOT NULL")
    List<Account> findForScheduling(Long locationId);

    @Query("SELECT new com.vindie.sunshine_ss.security.record.User(c.owner.id, c.owner.name, c.email, c.owner.lang, c.owner.gender, c.owner.premTill) " +
            "FROM Cread c " +
            "WHERE c.email = :email")
    Optional<User> findUserByEmail(String email);
}
