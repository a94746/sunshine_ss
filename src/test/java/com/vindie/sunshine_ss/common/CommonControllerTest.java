package com.vindie.sunshine_ss.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vindie.sunshine_ss.base.WithMvcAndSocket;
import com.vindie.sunshine_ss.common.dto.ChatPref;
import com.vindie.sunshine_ss.common.email.EmailService;
import com.vindie.sunshine_ss.common.service.VersionUtils;
import com.vindie.sunshine_ss.common.timers.queue.QueueParserTimer;
import com.vindie.sunshine_ss.location.Location;
import com.vindie.sunshine_ss.queue.dto.EventLine;
import com.vindie.sunshine_ss.ui_dto.UiLoginOpeningDialog;
import com.vindie.sunshine_ss.ui_dto.UiSettings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;
import java.util.List;

import static com.vindie.sunshine_ss.security.config.RequestFilter.MY_HEADER_NAME;
import static com.vindie.sunshine_ss.utils.DataUtils.getRandomElement;
import static com.vindie.sunshine_ss.utils.DataUtils.index;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommonControllerTest extends WithMvcAndSocket {
    @Autowired
    private VersionUtils versionUtils;
    @Autowired
    private EmailService emailService;
    @Autowired
    QueueParserTimer queueParserTimer;

    @Test
    void check_unique_email_positive() throws Exception {
        mvc.perform(get("/before_auth/check_unique_email")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .param("email", FAKER.internet().emailAddress() + index++))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void check_unique_email_negative() throws Exception {
        mvc.perform(get("/before_auth/check_unique_email")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .param("email", account.getCread().getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void the_same_version_test() throws Exception {
        String versionOs = getRandomVersionOs();
        mvc.perform(get("/before_auth/check_relevant_version")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .param("version", versionOs))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void above_version_test() throws Exception {
        String versionOs = getRandomVersionOs();
        String version = versionOs.substring(0, versionOs.length() - 3)
                + (Integer.parseInt(versionOs.substring(versionOs.length() - 3, versionOs.length() - 2)) + 1)
                + versionOs.substring(versionOs.length() - 2);

        mvc.perform(get("/before_auth/check_relevant_version")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .param("version", version))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void below_version_test() throws Exception {
        String versionOs = getRandomVersionOs();
        String version = versionOs.substring(0, versionOs.length() - 3)
                + (Integer.parseInt(versionOs.substring(versionOs.length() - 3, versionOs.length() - 2)) - 1)
                + versionOs.substring(versionOs.length() - 2);

        mvc.perform(get("/before_auth/check_relevant_version")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .param("version", version))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void email_code_positive() throws Exception {
        String email = FAKER.internet().emailAddress();
        mvc.perform(get("/before_auth/send_email_code")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .param("email", email))
                .andExpect(status().isOk());
        Integer code = emailService.cache.get(email);
        assertNotNull(code);

        mvc.perform(get("/before_auth/check_email_code")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .param("email", email)
                        .param("code", code.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void email_code_negative() throws Exception {
        String email = FAKER.internet().emailAddress();
        mvc.perform(get("/before_auth/send_email_code")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .param("email", email))
                .andExpect(status().isOk());
        Integer code = emailService.cache.get(email) - 1;
        assertNotNull(code);

        mvc.perform(get("/before_auth/check_email_code")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .param("email", email)
                        .param("code", code.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void get_settings_test() throws Exception {
        mvc.perform(get("/common/settings")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader()))
                .andExpect(status().isOk())
                .andExpect(modelMatches(UiSettings.class, s -> {
                    assertEquals(properties.ttl.uiPicCacheTTL, s.getPicCacheTTL());
                }));
    }

    @Test
    void get_login_window_notifs() throws Exception {
        EventLine eventLine = dataUtils
                .newTypicalEventLine(account.getId(), null, false, true);
        EventLine eventLine2 = dataUtils
                .newTypicalEventLine(account.getId(), null, false, true);
        eventLineRepo.saveAll(List.of(eventLine, eventLine2));
        queueParserTimer.timer();
        assertEquals(2, eventLineRepo.findAll().size());

        mvc.perform(get("/common/login_opening_dialogs")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader()))
                .andExpect(status().isOk())
                .andExpect(modelMatches(new TypeReference<List<UiLoginOpeningDialog>>() {}, l -> {
                    assertEquals(2, l.size());
                    assertNotEquals(l.get(0).getText(), l.get(1).getText());
                }));
        assertEquals(2, eventLineRepo.findAll().size());
        assertEquals(0, queueElementRepo.findAll().size());

        mvc.perform(get("/common/login_opening_dialogs")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader()))
                .andExpect(status().isOk())
                .andExpect(modelMatches(new TypeReference<List<UiLoginOpeningDialog>>() {}, l -> {
                    assertEquals(0, l.size());
                }));
    }

    @Test
    void get_chatPrefs() throws Exception {
        mvc.perform(get("/common/chat_prefs")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader()))
                .andExpect(status().isOk())
                .andExpect(modelMatches(new TypeReference<List<ChatPref>>() {}, l -> {
                    assertEquals(ChatPref.values().length, l.size());
                }));
    }

    @Test
    void get_locations() throws Exception {
        mvc.perform(get("/common/locations")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader()))
                .andExpect(status().isOk())
                .andExpect(modelMatches(new TypeReference<List<Location>>() {}, l -> {
                    assertEquals(locationRepo.findAll().size(), l.size());
                }));
    }

    @Test
    void get_lastscheduling() throws Exception {
        mvc.perform(get("/common/last_scheduling")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader()))
                .andExpect(status().isOk())
                .andExpect(modelMatches(LocalDateTime.class, t -> {
                    assertEquals(locationRepo.findById(account.getLocation().getId()).get().getLastScheduling().getHour(), t.getHour());
                }));
    }

    private String getRandomVersionOs() {
        return getRandomElement(List.of(properties.androidLeastVersion, properties.iosLeastVersion));
    }
}
