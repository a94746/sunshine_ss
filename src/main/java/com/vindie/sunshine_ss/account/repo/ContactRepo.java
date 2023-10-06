package com.vindie.sunshine_ss.account.repo;

import com.vindie.sunshine_ss.account.dto.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepo extends JpaRepository<Contact, Long> {

    List<Contact> findAllByOwnerId(Long accountId);
}
