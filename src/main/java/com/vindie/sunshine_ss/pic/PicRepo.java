package com.vindie.sunshine_ss.pic;

import com.vindie.sunshine_ss.ui_dto.UiPicInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface PicRepo extends JpaRepository<Pic, Long> {

    List<Pic> findAllByOwnerId(Long ownerId);

    @Query("SELECT new com.vindie.sunshine_ss.ui_dto.UiPicInfo(p.id, p.lastModified) " +
            "FROM Pic p " +
            "WHERE p.owner.id = :ownerId")
    List<UiPicInfo> findAllPicInfosByOwnerId(Long ownerId);

    @Query("SELECT COUNT(1) FROM Pic p " +
            "WHERE p.owner.id = :ownerId")
    int countByOwnerId(Long ownerId);

    @Modifying
    @Query("DELETE FROM Pic p " +
            "WHERE p.id = :id " +
            "AND p.owner.id = :ownerId")
    void deleteByIdAndOwnerId(Long id, Long ownerId);

    List<Pic> findAllByOwnerIdAndIdIn(Long ownerId, List<Long> ids);
}