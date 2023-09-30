package com.vindie.sunshine_ss.security.service;

import com.vindie.sunshine_ss.security.record.JwtAuthenticationResponse;
import com.vindie.sunshine_ss.security.record.SignUpRequest;
import com.vindie.sunshine_ss.security.record.SigninRequest;

public interface AuthenticationService {
    JwtAuthenticationResponse signup(SignUpRequest request);

    JwtAuthenticationResponse signin(SigninRequest request);
}
