package com.vindie.sunshine_ss.account;

import com.vindie.sunshine_ss.base.WithMvc;
import com.vindie.sunshine_ss.common.record.UiContact;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static com.vindie.sunshine_ss.security.config.RequestFilter.MY_HEADER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContactControllerTest extends WithMvc {

    @Test
    void create() throws Exception {
        account.setContacts(new ArrayList<>());
        accountRepo.save(account);
        assertEquals(0, contactRepo.findAllByOwnerId(account.getId()).size());
        var key = "sdvsdvsv";
        var value = "sadvsdvddvsdvsv";

        var request = UiContact.builder()
                .key(key)
                .value(value)
                .build();
        mvc.perform(post("/contact")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(contactRepo.findAllByOwnerId(account.getId()).get(0).getId()+""));
        assertEquals(1, contactRepo.findAllByOwnerId(account.getId()).size());
        assertEquals(key, contactRepo.findAllByOwnerId(account.getId()).get(0).getKey());
        assertEquals(value, contactRepo.findAllByOwnerId(account.getId()).get(0).getValue());
        assertEquals(account.getId(), contactRepo.findAllByOwnerId(account.getId()).get(0).getOwner().getId());
    }

    @Test
    void delete() throws Exception {
        account.setContacts(new ArrayList<>());
        accountRepo.save(account);
        assertEquals(0, contactRepo.findAllByOwnerId(account.getId()).size());
        var key = "sdvsdvsv";
        var value = "sadvsdvddvsdvsv";

        var request = UiContact.builder()
                .key(key)
                .value(value)
                .build();
        var id = mvc.perform(post("/contact")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(1, contactRepo.findAllByOwnerId(account.getId()).size());

        mvc.perform(MockMvcRequestBuilders.delete("/contact")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals(0, contactRepo.findAllByOwnerId(account.getId()).size());
    }

    @Test
    void update() throws Exception {
        account.setContacts(new ArrayList<>());
        accountRepo.save(account);
        assertEquals(0, contactRepo.findAllByOwnerId(account.getId()).size());
        var key = "sdvsdvsv";
        var value = "sadvsdvddvsdvsv";

        var request = UiContact.builder()
                .key(key)
                .value(value)
                .build();
        var id = mvc.perform(post("/contact")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(1, contactRepo.findAllByOwnerId(account.getId()).size());

        var key2 = "sd23223vsdvsv";
        var value2 = "sadvs234dvddvsdvsv";

        var request2 = UiContact.builder()
                .id(Long.valueOf(id))
                .key(key2)
                .value(value2)
                .build();
        mvc.perform(put("/contact")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(request2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(1, contactRepo.findAllByOwnerId(account.getId()).size());
        assertEquals(key2, contactRepo.findAllByOwnerId(account.getId()).get(0).getKey());
        assertEquals(value2, contactRepo.findAllByOwnerId(account.getId()).get(0).getValue());
        assertEquals(account.getId(), contactRepo.findAllByOwnerId(account.getId()).get(0).getOwner().getId());
    }

}
