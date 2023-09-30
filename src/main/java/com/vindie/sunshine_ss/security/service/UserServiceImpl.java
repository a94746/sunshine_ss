package com.vindie.sunshine_ss.security.service;

import com.vindie.sunshine_ss.account.repo.AccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final AccountRepo accountRepo;
    @Override
    public UserDetailsService userDetailsService() {
        return email -> accountRepo.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
