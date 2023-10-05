package com.vindie.sunshine_ss.pic;

import com.vindie.sunshine_ss.common.record.UiPicInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface PicRepo extends JpaRepository<Pic, Long> {

    List<Pic> findAllByOwnerId(Long ownerId);

    @Query("SELECT new com.vindie.sunshine_ss.common.record.UiPicInfo(p.id, p.lastModified) " +
            "FROM Pic p " +
            "WHERE p.owner.id = :ownerId")
    List<UiPicInfo> findAllPicInfosByOwnerId(Long ownerId);
}