package com.vindie.sunshine_ss.account;

import com.vindie.sunshine_ss.base.WithMvcAndSocket;
import com.vindie.sunshine_ss.common.dto.ChatPref;
import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Language;
import com.vindie.sunshine_ss.common.dto.Relation;
import com.vindie.sunshine_ss.common.email.EmailService;
import com.vindie.sunshine_ss.security.record.ChangeEmail;
import com.vindie.sunshine_ss.ui_dto.UiMyAccount;
import com.vindie.sunshine_ss.ui_dto.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static com.vindie.sunshine_ss.account.service.AccountService.isPrem;
import static com.vindie.sunshine_ss.security.config.RequestFilter.MY_HEADER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest extends WithMvcAndSocket {

    @Autowired
    EmailService emailService;

    @Test
    void edit_acc_test() throws Exception {
        var accsBefore = accountRepo.findAll();
        var json = mvc.perform(get("/account/my")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var acc = MAPPER.readValue(json, UiMyAccount.class);
        var newName = "svfsdvs.sk23dv";
        acc.setName(newName);

        mvc.perform(put("/account/my")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(acc))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get("/account/my")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isOk())
                .andExpect(modelMatches(UiMyAccount.class, a -> {
                    assertEquals(newName, a.getName());
                }));
        assertEquals(accsBefore.size(), accountRepo.findAll().size());
    }

    @Test
    void take_info_test() throws Exception {
        var appVersion = "vvsddvsdvsd";
        var lang = account.getLang() == Language.EN
                ? Language.RU
                : Language.EN;
        var device = account.getDevices().get(0);
        var request = UserInfo.builder()
                .uniqueId(device.getUniqueId())
                .lang(lang)
                .appVersion(appVersion)
                .build();
        mvc.perform(post("/account/take_info")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                .content(MAPPER.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals(appVersion, deviceRepo.findById(device.getId()).get().getAppVersion());
        assertEquals(lang, accountRepo.findById(account.getId()).get().getLang());
        assertTrue(accountRepo.findById(account.getId()).get().getLastPresence().isAfter(account.getLastPresence()));
    }

    @Test
    @Transactional
    void get_my_test() throws Exception {
        mvc.perform(get("/account/my")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isOk())
                .andExpect(modelMatches(UiMyAccount.class, a -> {
                    assertEquals(account.getContacts().size(), a.getContacts().size());
                    assertEquals(account.getPics().size(), a.getPicInfos().size());
                    assertEquals(account.getCread().getEmail(), a.getEmail());
                    assertEquals(account.getBday(), a.getBday());
                    assertEquals(ChatPref.values().length, a.getFilter().getChatPrefs().size());
                    assertEquals(Relation.values().length, a.getFilter().getRelationsWithGenders().size());
                    assertEquals(Gender.values().length, a.getFilter().getRelationsWithGenders().get(Relation.ACQUAINTANCE).size());
                    assertEquals(account.getName(), a.getName());
                    assertEquals(account.getDescription(), a.getDescription());
                    assertEquals(account.getLocation().getId(), a.getLocationId());
                    assertEquals(isPrem(account), a.getPrem());
                    assertEquals(account.getGender(), a.getGender());
                    assertEquals(isPrem(account) ? account.getRating() : null, a.getRating());
                    assertEquals(isPrem(account) ? account.getPremMatchesNum() : account.getMatchesNum(), a.getActualMatchesNum());
                }));
    }

    @Test
    void change_email_test() throws Exception {
        var creadsBefore = creadRepo.findAll();
        var newEmail = "sdsvdsvd@sdvsv.rio";

        mvc.perform(get("/before_auth/send_email_code")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .param("email", newEmail))
                .andExpect(status().isOk());
        Integer code = emailService.cache.get(newEmail);

        var request = ChangeEmail.builder()
                .newEmail(newEmail)
                .emailCode(code)
                .build();
        mvc.perform(post("/account/change_email")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals(account.getId(), creadRepo.findFirstByEmail(newEmail).get().getOwner().getId());
        assertTrue(creadRepo.findFirstByEmail(account.getCread().getEmail()).isEmpty());
        assertEquals(creadsBefore.size(), creadRepo.findAll().size());
    }

    @Test
    void delete_acc_test() throws Exception {
        var accsBefore = accountRepo.findAll();
        mvc.perform(delete("/account/my")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isOk());

        mvc.perform(get("/account/my")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isUnauthorized());
        assertEquals(accsBefore.size(), accountRepo.findAll().size());
        assertTrue(accountRepo.findById(account.getId()).orElseThrow().getDeleted());
    }
}
