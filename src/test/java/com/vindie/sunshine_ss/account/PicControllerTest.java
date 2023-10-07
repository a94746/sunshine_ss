package com.vindie.sunshine_ss.account;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vindie.sunshine_ss.base.WithMvc;
import com.vindie.sunshine_ss.common.record.UiPic;
import com.vindie.sunshine_ss.common.record.UiPicInfo;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.vindie.sunshine_ss.security.config.RequestFilter.MY_HEADER_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PicControllerTest extends WithMvc {

    @Test
    void create() throws Exception {
        account.setPics(new ArrayList<>());
        accountRepo.save(account);
        assertEquals(0, picService.findAll()
                .stream()
                .filter(p -> p.getOwner().getId().equals(account.getId()))
                .count());
        var file = "sdvsdvsv".getBytes();

        var request = UiPic.builder()
                .file(file)
                .build();
        mvc.perform(post("/pic")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(modelMatches(UiPicInfo.class, pi -> {
                    assertNotNull(pi.getLastModified());
                    assertNotNull(pi.getId());
                }));
        assertEquals(1, picService.findAll()
                .stream()
                .filter(p -> p.getOwner().getId().equals(account.getId()))
                .count());
        assertTrue(Arrays.equals(file, picService.findAll()
                .stream()
                .filter(p -> p.getOwner().getId().equals(account.getId()))
                .toList().get(0).getFile()));
        assertTrue(picService.findAll()
                .stream()
                .filter(p -> p.getOwner().getId().equals(account.getId()))
                .toList().get(0).getLastModified().isAfter(LocalDateTime.now().minusMinutes(1)));
        assertEquals(account.getId(), picService.findAll()
                .stream()
                .filter(p -> p.getOwner().getId().equals(account.getId()))
                .toList().get(0).getOwner().getId());
    }

    @Test
    void delete() throws Exception {
        account.setPics(new ArrayList<>());
        accountRepo.save(account);
        assertEquals(0, picService.findAll()
                .stream()
                .filter(p -> p.getOwner().getId().equals(account.getId()))
                .count());
        var file = "sdvsdvsv".getBytes();

        var request = UiPic.builder()
                .file(file)
                .build();
        var json = mvc.perform(post("/pic")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(modelMatches(UiPicInfo.class, pi -> {
                    assertNotNull(pi.getLastModified());
                    assertNotNull(pi.getId());
                }))
                .andReturn()
                .getResponse()
                .getContentAsString();
        var result = MAPPER.readValue(json, UiPicInfo.class);

        assertEquals(1, picService.findAll()
                .stream()
                .filter(p -> p.getOwner().getId().equals(account.getId()))
                .count());

        mvc.perform(MockMvcRequestBuilders.delete("/pic")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(result.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals(0, picService.findAll()
                .stream()
                .filter(p -> p.getOwner().getId().equals(account.getId()))
                .count());
    }

    @Test
    void get() throws Exception {
        var p1 = picService.saveRepo(dataUtils.newTypicalPic(account));
        var p2 = picService.saveRepo(dataUtils.newTypicalPic(account));
        var p3 = picService.saveRepo(dataUtils.newTypicalPic(account));
        account.setPics(List.of(p1, p2, p3));
        accountRepo.save(account);
        assertEquals(3, picService.findAll()
                .stream()
                .filter(p -> p.getOwner().getId().equals(account.getId()))
                .count());

        mvc.perform(MockMvcRequestBuilders.get("/pic")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(List.of(p1.getId(), p2.getId())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(modelMatches(new TypeReference<List<UiPic>>() {}, l -> {
                    assertEquals(2, l.size());
                    l.forEach(p -> {
                        assertNotNull(p.getId());
                        assertNotNull(p.getLastModified());
                    });
                }));

        assertEquals(3, picService.findAll()
                .stream()
                .filter(p -> p.getOwner().getId().equals(account.getId()))
                .count());
    }

}
