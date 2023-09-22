package com.vindie.sunshine_ss.filter.repo;

import com.vindie.sunshine_ss.filter.dto.RelationWithGenders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationWithGendersRepo extends JpaRepository<RelationWithGenders, Long> {
}
