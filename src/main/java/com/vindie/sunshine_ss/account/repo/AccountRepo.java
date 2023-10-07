package com.vindie.sunshine_ss.account.repo;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.common.dto.Language;
import com.vindie.sunshine_ss.security.record.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {

    List<Account> findAllByLocationIdAndDeletedFalse(Long locationId);

    Optional<Account> findByIdAndDeletedFalse(Long id);

    @Query("SELECT a.id FROM Account a " +
            "WHERE a.lastPresence < :older")
    List<Long> findOlder(LocalDateTime older);

    @Query("SELECT a.location.lastScheduling FROM Account a " +
            "WHERE a.id = :accId")
    LocalDateTime getLastScheduling(Long accId);

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

    @Modifying
    @Query("UPDATE Account a " +
            "SET a.views = a.views + 1 " +
            "WHERE a.id IN (:ids)")
    void incrementViews(Collection<Long> ids);

    @Modifying
    @Query("UPDATE Account a " +
            "SET a.likes = a.likes + 1 " +
            "WHERE a.id IN (:ids)")
    void incrementLikes(Collection<Long> ids);

    @Query("SELECT a FROM Account a " +
            "LEFT JOIN FETCH a.matchesOwner " +
            "LEFT JOIN a.matchesOwner.partner " +
            "LEFT JOIN a.filter " +
            "LEFT JOIN a.filter.relationsWithGenders " +
            "LEFT JOIN a.filter.chatPrefs " +
            "LEFT JOIN a.filter.relationsWithGenders.genders " +
            "WHERE a.location.id = :locationId " +
            "AND a.filter IS NOT NULL " +
            "AND a.deleted = FALSE")
    List<Account> findForScheduling(Long locationId);

    @Query("SELECT a FROM Account a " +
            "LEFT JOIN FETCH a.filter " +
            "LEFT JOIN FETCH a.cread " +
            "LEFT JOIN FETCH a.contacts " +
            "LEFT JOIN a.filter.relationsWithGenders " +
            "LEFT JOIN a.filter.chatPrefs " +
            "LEFT JOIN a.filter.relationsWithGenders.genders " +
            "WHERE a.id = :id " +
            "AND a.deleted = FALSE")
    Optional<Account> findForMyAccount(Long id);

    @Query("SELECT new com.vindie.sunshine_ss.security.record.User(c.owner.id, c.owner.name, c.email, c.pass, " +
            "c.owner.lang, c.owner.gender, c.owner.premTill) " +
            "FROM Cread c " +
            "WHERE c.email = :email " +
            "AND c.owner.deleted = FALSE")
    Optional<User> findUserByEmail(String email);

    @Query("SELECT a FROM Account a " +
            "LEFT JOIN FETCH a.cread " +
            "LEFT JOIN FETCH a.devices")
    List<Account> findWithCreadAndDevices();

    @Modifying
    @Query("UPDATE Account a " +
            "SET a.lang = :lang, " +
            "a.lastPresence = :lastPresence " +
            "WHERE a.id = :id ")
    void takeInfo(Long id, Language lang, LocalDateTime lastPresence);
}
