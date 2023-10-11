package com.vindie.sunshine_ss.account.controller;

import com.vindie.sunshine_ss.account.repo.CreadRepo;
import com.vindie.sunshine_ss.account.service.AccountService;
import com.vindie.sunshine_ss.common.dto.UiKey;
import com.vindie.sunshine_ss.common.dto.exception.SunshineException;
import com.vindie.sunshine_ss.common.email.EmailService;
import com.vindie.sunshine_ss.security.record.ChangeEmail;
import com.vindie.sunshine_ss.security.service.CurUserService;
import com.vindie.sunshine_ss.ui_dto.UiMyAccount;
import com.vindie.sunshine_ss.ui_dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;
    private final CreadRepo creadRepo;
    private final EmailService emailService;

    @GetMapping("/get_my")
    public UiMyAccount getMy() {
        return accountService.getMyAccount(CurUserService.get());
    }

    @PutMapping("/edit_my")
    public void editMy(@RequestBody UiMyAccount uiMyAccount) {
        UiKey error = accountService.editMy(uiMyAccount, CurUserService.get());
        if (error != null)
            throw new SunshineException(error);
    }

    @PostMapping("/take_info")
    public void takeInfo(@RequestBody UserInfo info) {
        info.validate();
        accountService.takeInfo(info, CurUserService.get());
    }

    @PostMapping("/change_email")
    public void changeEmail(@RequestBody ChangeEmail request) {
        request.validate();
        if (creadRepo.findFirstByEmail(request.getNewEmail()).isPresent())
            throw new SunshineException(UiKey.NO_UNIQUE_EMAIL);
        if (!emailService.isCorrectEmailCode(request.getNewEmail(), request.getEmailCode()))
            throw new SunshineException(UiKey.NOT_CORRECT_EMAIL_CODE);
        if (!emailService.isValid(request.getNewEmail()))
            throw new SunshineException(UiKey.NOT_A_EMAIL);
        accountService.changeEmail(request.getNewEmail(), CurUserService.get());
    }
}
