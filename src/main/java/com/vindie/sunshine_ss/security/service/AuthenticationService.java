package com.vindie.sunshine_ss.security.service;

import com.vindie.sunshine_ss.security.record.*;

public interface AuthenticationService {
    JwtAuthenticationResponse signup(SignUpRequest request);
    JwtAuthenticationResponse signin(SigninRequest request);
    void recoveryPassword(PasswordRecovery request);
    void logout(User user);

}
