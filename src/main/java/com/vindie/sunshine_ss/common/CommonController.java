package com.vindie.sunshine_ss.common;

import com.vindie.sunshine_ss.account.repo.CreadRepo;
import com.vindie.sunshine_ss.common.email.EmailService;
import com.vindie.sunshine_ss.common.service.VersionUtils;
import com.vindie.sunshine_ss.security.service.AuthenticationService;
import com.vindie.sunshine_ss.security.service.CurUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class CommonController {
    private CreadRepo creadRepo;
    private VersionUtils versionUtils;
    private EmailService emailService;
    private AuthenticationService authenticationService;

    @GetMapping("/before_auth/check_unique_email")
    public boolean checkUniqueEmail(@RequestParam("email") String email) {
        return creadRepo.findFirstByEmail(email).isEmpty();
    }

    @GetMapping("/before_auth/check_relevant_version")
    public boolean checkRelevantVersion(@RequestParam("version") String version) {
        return versionUtils.isRelevantVersion(version);
    }

    @GetMapping("/before_auth/send_email_code")
    public void sendEmailCode(@RequestParam("email") String email) {
        emailService.sendEmailCode(email);
    }

    @GetMapping("/before_auth/check_email_code")
    public boolean sendEmailCode(@RequestParam("email") String email,
                                 @RequestParam("code") Integer code) {
        return emailService.isCorrectEmailCode(email, code);
    }

    @GetMapping("/test")
    public String test() {
        return "Hello sunshine! " + CurUserService.get().getName();
    }

    @PostMapping("/logout_easy")
    public void logout() {
        authenticationService.logout(CurUserService.get());
    }
}
