package com.vindie.sunshine_ss.account.repo;

import com.vindie.sunshine_ss.account.dto.Device;
import com.vindie.sunshine_ss.security.record.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepo extends JpaRepository<Device, Long> {

    List<Device> findAllByLogoutOwnerId(Long ownerIdLogout);
    List<Device> findAllByOwnerId(Long ownerId);
    List<Device> findAllByOwnerIdIn(List<Long> ownerIds);
    Optional<Device> findFirstByUniqueId(String uniqueId);

    @Query("SELECT new com.vindie.sunshine_ss.security.record.User(d.owner.id, d.owner.name, d.owner.cread.email, " +
            "d.owner.cread.pass, d.owner.lang, d.owner.gender, d.owner.premTill) " +
            "FROM Device d " +
            "WHERE d.uniqueId = :uniqueId " +
            "AND d.owner.deleted = FALSE")
    Optional<User> findUserByUniqueId(String uniqueId);

    @Modifying
    @Query("UPDATE Device d " +
            "SET d.logoutOwnerId = d.owner.id, " +
            "d.owner = NULL " +
            "WHERE d.owner.id = :ownerId")
    void logoutDevicesByOwnerId(Long ownerId);
}
