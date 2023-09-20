package com.vindie.sunshine_ss.pic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PicRepo extends JpaRepository<Pic, Long> {
}