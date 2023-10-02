package com.vindie.sunshine_ss.security;

import com.vindie.sunshine_ss.account.repo.CreadRepo;
import com.vindie.sunshine_ss.common.dto.UiKey;
import com.vindie.sunshine_ss.common.dto.exception.SunshineException;
import com.vindie.sunshine_ss.common.email.EmailService;
import com.vindie.sunshine_ss.security.record.JwtAuthenticationResponse;
import com.vindie.sunshine_ss.security.record.PasswordRecovery;
import com.vindie.sunshine_ss.security.record.SignUpRequest;
import com.vindie.sunshine_ss.security.record.SigninRequest;
import com.vindie.sunshine_ss.security.service.AuthenticationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class LoginController {
    private AuthenticationService authenticationService;
    private CreadRepo creadRepo;
    private EmailService emailService;

    @PostMapping("/signin")
    public JwtAuthenticationResponse signin(@RequestBody SigninRequest request) {
        return authenticationService.signin(request);
    }

    @PostMapping("/signup")
    public JwtAuthenticationResponse signup(@RequestBody SignUpRequest request) {
        if (creadRepo.findFirstByEmail(request.email).isPresent())
            throw new SunshineException(UiKey.NO_UNIQUE_EMAIL);
        if (!emailService.isCorrectEmailCode(request.email, request.emailCode))
            throw new SunshineException(UiKey.NOT_CORRECT_EMAIL_CODE);
        if (!emailService.isValid(request.email))
            throw new SunshineException(UiKey.NOT_A_EMAIL);
        return authenticationService.signup(request);
    }

    @PostMapping("/password_recovery")
    public void passwordRecovery(@RequestBody PasswordRecovery request) {
        if (creadRepo.findFirstByEmail(request.email).isEmpty())
            throw new SunshineException(UiKey.NO_EXISTING_EMAIL);
        if (!emailService.isCorrectEmailCode(request.email, request.emailCode))
            throw new SunshineException(UiKey.NOT_CORRECT_EMAIL_CODE);
        authenticationService.recoveryPassword(request);
    }
}
