package com.vindie.sunshine_ss.account.repo;

import com.vindie.sunshine_ss.account.dto.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {
}
