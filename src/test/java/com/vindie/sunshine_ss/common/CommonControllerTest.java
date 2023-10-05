package com.vindie.sunshine_ss.common;

import com.vindie.sunshine_ss.base.WithMvc;
import com.vindie.sunshine_ss.common.email.EmailService;
import com.vindie.sunshine_ss.common.record.UiSettings;
import com.vindie.sunshine_ss.common.service.VersionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.util.List;

import static com.vindie.sunshine_ss.security.config.RequestFilter.MY_HEADER_NAME;
import static com.vindie.sunshine_ss.utils.DataUtils.getRandomElement;
import static com.vindie.sunshine_ss.utils.DataUtils.index;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommonControllerTest extends WithMvc {
    @Autowired
    private VersionUtils versionUtils;
    @Autowired
    private EmailService emailService;

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
        mvc.perform(get("/settings")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader()))
                .andExpect(status().isOk())
                .andExpect(modelMatches(UiSettings.class, s -> {
                    assertEquals(propService.uiPicCacheTTL, s.getPicCacheTTL());
                }));
    }

    private String getRandomVersionOs() {
        return getRandomElement(List.of(propService.androidLeastVersion, propService.iosLeastVersion));
    }
}
