package com.vindie.sunshine_ss.security;

import com.vindie.sunshine_ss.base.WithMvc;
import com.vindie.sunshine_ss.common.email.EmailService;
import com.vindie.sunshine_ss.security.record.PasswordRecovery;
import com.vindie.sunshine_ss.security.record.SignUpRequest;
import com.vindie.sunshine_ss.security.record.SigninRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static com.vindie.sunshine_ss.security.config.RequestFilter.MY_HEADER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends WithMvc {
    @Autowired
    EmailService emailService;

    @Test
    void get_to_security_space_without_my_token() throws Exception {
        mvc.perform(get("/test")
                .header(HttpHeaders.AUTHORIZATION, getJwtHeader()))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void get_to_security_space_without_jwt_token() throws Exception {
        mvc.perform(get("/test")
                .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void get_to_security_space_with_token() throws Exception {
        mvc.perform(get("/test")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello sunshine! " + account.getName()));
    }

    @Test
    void get_to_security_space_with_token_deleted() throws Exception {
        account.setDeleted(true);
        assertTrue(accountRepo.save(account).getDeleted());
        mvc.perform(get("/test")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signup_the_same_email() throws Exception {
        var beforeAccs = accountRepo.findAll();
        var newAcc = dataUtils.newTypicalAccount(account.getLocation(), false);
        var request = SignUpRequest.builder()
                .email(account.getCread().getEmail())
                .name(newAcc.getName())
                .description(newAcc.getDescription())
                .gender(newAcc.getGender())
                .bday(newAcc.getBday())
                .lang(newAcc.getLang())
                .locationName(newAcc.getLocation().getName())
                .password(PASS)
                .uniqueId(newAcc.getDevices().get(0).getUniqueId())
                .appVersion(newAcc.getDevices().get(0).getAppVersion())
                .build();
        var body = MAPPER.writeValueAsString(request);
        mvc.perform(post("/auth/signup")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        assertEquals(beforeAccs.size(), accountRepo.findAll().size());
    }

    @Test
    void signup_new() throws Exception {
        var newAcc = dataUtils.newTypicalAccount(account.getLocation(), false);

        String email = newAcc.getCread().getEmail();
        mvc.perform(get("/before_auth/send_email_code")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .param("email", email))
                .andExpect(status().isOk());
        Integer code = emailService.cache.get(email);

        var beforeAccs = accountRepo.findAll();
        var request = SignUpRequest.builder()
                .email(email)
                .emailCode(code)
                .name(newAcc.getName())
                .description(newAcc.getDescription())
                .gender(newAcc.getGender())
                .bday(newAcc.getBday())
                .lang(newAcc.getLang())
                .locationName(newAcc.getLocation().getName())
                .password(PASS)
                .uniqueId(newAcc.getDevices().get(0).getUniqueId())
                .appVersion(newAcc.getDevices().get(0).getAppVersion())
                .build();
        var body = MAPPER.writeValueAsString(request);
        mvc.perform(post("/auth/signup")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals(beforeAccs.size() + 1, accountRepo.findAll().size());
    }

    @Test
    @Transactional
    void signup_new_on_existing_device() throws Exception {
        var newAcc = dataUtils.newTypicalAccount(account.getLocation(), false);

        String email = newAcc.getCread().getEmail();
        mvc.perform(get("/before_auth/send_email_code")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .param("email", email))
                .andExpect(status().isOk());
        Integer code = emailService.cache.get(email);

        var beforeAccs = accountRepo.findAll();
        var request = SignUpRequest.builder()
                .email(email)
                .emailCode(code)
                .name(newAcc.getName())
                .description(newAcc.getDescription())
                .gender(newAcc.getGender())
                .bday(newAcc.getBday())
                .lang(newAcc.getLang())
                .locationName(newAcc.getLocation().getName())
                .password(PASS)
                .uniqueId(account.getDevices().get(0).getUniqueId())
                .appVersion(newAcc.getDevices().get(0).getAppVersion())
                .build();
        var body = MAPPER.writeValueAsString(request);
        mvc.perform(post("/auth/signup")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals(beforeAccs.size() + 1, accountRepo.findAll().size());
        assertEquals(request.email, deviceRepo.findFirstByUniqueId(request.uniqueId).get().getOwner().getCread().getEmail());
    }

    @Test
    void signin_easy() throws Exception {
        var request = SigninRequest.builder()
                .uniqueId(account.getDevices().get(0).getUniqueId())
                .build();
        var response = mvc.perform(post("/auth/signin")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void password_recovery() throws Exception {
        String newPass = "sdvsvnsiodis8";
        String email = account.getCread().getEmail();
        mvc.perform(get("/before_auth/send_email_code")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .param("email", email))
                .andExpect(status().isOk());
        Integer code = emailService.cache.get(email);

        var request = PasswordRecovery.builder()
                .email(email)
                .emailCode(code)
                .newPassword(newPass)
                .build();
        mvc.perform(post("/auth/password_recovery")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var request2 = SigninRequest.builder()
                .email(email)
                .pass(newPass)
                .uniqueId(account.getDevices().get(0).getUniqueId())
                .build();
        mvc.perform(post("/auth/signin")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(request2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void logout() throws Exception {
        var devicesBefore = deviceRepo.findAllByOwnerId(account.getId()).size();
        mvc.perform(post("/logout_easy")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isOk());
        assertTrue(accountRepo.findById(account.getId()).isPresent());
        assertEquals(0, deviceRepo.findAllByOwnerId(account.getId()).size());
        assertEquals(devicesBefore, deviceRepo.findAllByLogoutOwnerId(account.getId()).size());

        var request = SigninRequest.builder()
                .uniqueId(account.getDevices().get(0).getUniqueId())
                .build();
        mvc.perform(post("/auth/signin")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_another_acc_the_same_device() throws Exception {
        var accountDevicesBefore = deviceRepo.findAllByOwnerId(account.getId());

        var newAcc = dataUtils.newTypicalAccount(account.getLocation(), false);
        accountRepo.save(newAcc);
        var devicesBefore = deviceRepo.findAll();
        var oldDevice = account.getDevices().get(0);
        var request = SigninRequest.builder()
                .email(newAcc.getCread().getEmail())
                .pass(PASS)
                .uniqueId(oldDevice.getUniqueId())
                .build();
        mvc.perform(post("/auth/signin")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals(accountDevicesBefore.size() - 1, deviceRepo.findAllByOwnerId(account.getId()).size());
        assertEquals(newAcc.getId(), deviceRepo.findById(oldDevice.getId()).get().getOwner().getId());
        assertEquals(devicesBefore.size(), deviceRepo.findAll().size());
    }

}
