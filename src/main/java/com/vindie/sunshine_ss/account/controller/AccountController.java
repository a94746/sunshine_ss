package com.vindie.sunshine_ss.account.controller;

import com.vindie.sunshine_ss.account.repo.CreadRepo;
import com.vindie.sunshine_ss.account.service.AccountService;
import com.vindie.sunshine_ss.common.dto.UiKey;
import com.vindie.sunshine_ss.common.dto.exception.SunshineException;
import com.vindie.sunshine_ss.common.email.EmailService;
import com.vindie.sunshine_ss.common.service.AbstractController;
import com.vindie.sunshine_ss.security.record.ChangeEmail;
import com.vindie.sunshine_ss.ui_dto.UiMyAccount;
import com.vindie.sunshine_ss.ui_dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/account")
public class AccountController extends AbstractController {
    private final AccountService accountService;
    private final CreadRepo creadRepo;
    private final EmailService emailService;

    @GetMapping("/my")
    public UiMyAccount getMy() {
        return accountService.getMyAccount(getCurrentUser());
    }

    @PutMapping("/my")
    public void editMy(@RequestBody UiMyAccount uiMyAccount) {
        accountService.editMy(uiMyAccount, getCurrentUser());
    }

    @DeleteMapping("/my")
    public void deleteMy() {
        accountService.softDelete(getCurrentUserId());
    }

    @PostMapping("/take_info")
    public void takeInfo(@RequestBody UserInfo info) {
        info.validate();
        accountService.takeInfo(info, getCurrentUser());
    }

    @PostMapping("/change_email")
    public void changeEmail(@RequestBody ChangeEmail request) {
        validateChangeEmailRequest(request);
        accountService.changeEmail(request.getNewEmail(), getCurrentUser());
    }

    private void validateChangeEmailRequest(ChangeEmail request) {
        request.validate();
        if (creadRepo.findFirstByEmail(request.getNewEmail()).isPresent())
            throw new SunshineException(UiKey.NO_UNIQUE_EMAIL);
        if (!emailService.isCorrectEmailCode(request.getNewEmail(), request.getEmailCode()))
            throw new SunshineException(UiKey.NOT_CORRECT_EMAIL_CODE);
        if (!emailService.isValid(request.getNewEmail()))
            throw new SunshineException(UiKey.NOT_A_EMAIL);
    }
}
