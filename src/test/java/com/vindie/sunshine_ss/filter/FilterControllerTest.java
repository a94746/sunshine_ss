package com.vindie.sunshine_ss.filter;

import com.vindie.sunshine_ss.base.WithMvc;
import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Relation;
import com.vindie.sunshine_ss.common.record.UiMyAccount;
import com.vindie.sunshine_ss.filter.repo.RelationWithGendersRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.vindie.sunshine_ss.security.config.RequestFilter.MY_HEADER_NAME;
import static com.vindie.sunshine_ss.utils.DataUtils.getRandomElement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FilterControllerTest extends WithMvc {
    @Autowired
    RelationWithGendersRepo relationWithGendersRepo;
    @Test
    void edit_acc_test() throws Exception {
        var accsBefore = accountRepo.findAll();
        var json = mvc.perform(get("/account/get_my")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var filter = MAPPER.readValue(json, UiMyAccount.class).getFilter();

        Map<Relation, Map<Gender, Boolean>> relationsWithGenders = new EnumMap<>(Relation.class);
        for (Relation relation : Relation.values()) {
            Map<Gender, Boolean> genders = new EnumMap<>(Gender.class);
            for (Gender gender : Gender.values()) {
                boolean active = getRandomElement(List.of(Boolean.TRUE, Boolean.FALSE));
                genders.put(gender, active);
            }
            relationsWithGenders.put(relation, genders);
        }
        var newAgeFrom = (byte) 117;
        filter.setAgeFrom(newAgeFrom);
        filter.setRelationsWithGenders(relationsWithGenders);

        mvc.perform(put("/filter/edit_my")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(filter))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var json2 = mvc.perform(get("/account/get_my")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var filter2 = MAPPER.readValue(json2, UiMyAccount.class).getFilter();
        for (Relation relation : relationsWithGenders.keySet()) {
            for (Gender gender : relationsWithGenders.get(relation).keySet()) {
                assertEquals(relationsWithGenders.get(relation).get(gender),
                        filter2.getRelationsWithGenders().get(relation).get(gender));
            }
        }

        assertEquals(newAgeFrom, filter2.getAgeFrom());
        assertEquals(accsBefore.size(), accountRepo.findAll().size());
    }

    @Test
    void edit_acc_test2() throws Exception {
        var accsBefore = accountRepo.findAll();
        var json = mvc.perform(get("/account/get_my")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var filter = MAPPER.readValue(json, UiMyAccount.class).getFilter();

        Map<Relation, Map<Gender, Boolean>> relationsWithGenders = new EnumMap<>(Relation.class);
        for (Relation relation : Relation.values()) {
            Map<Gender, Boolean> genders = new EnumMap<>(Gender.class);
            for (Gender gender : Gender.values()) {
                boolean active = true;
                genders.put(gender, active);
            }
            relationsWithGenders.put(relation, genders);
        }
        filter.setRelationsWithGenders(relationsWithGenders);

        var r2gs1 = relationWithGendersRepo.findAll();
        mvc.perform(put("/filter/edit_my")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(filter))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertTrue(relationWithGendersRepo.findAll().size() > r2gs1.size());
        var r2gs2 = relationWithGendersRepo.findAll();

        var json2 = mvc.perform(get("/account/get_my")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var filter2 = MAPPER.readValue(json2, UiMyAccount.class).getFilter();

        Map<Relation, Map<Gender, Boolean>> relationsWithGenders2 = new EnumMap<>(Relation.class);
        Map<Gender, Boolean> v2 = new EnumMap<>(Gender.class);
        v2.put(Gender.FEMALE, Boolean.TRUE);
        relationsWithGenders2.put(Relation.ACQUAINTANCE, v2);
        filter2.setRelationsWithGenders(relationsWithGenders2);

        mvc.perform(put("/filter/edit_my")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(filter2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertTrue(relationWithGendersRepo.findAll().size() < r2gs2.size());
        assertEquals(accsBefore.size(), accountRepo.findAll().size());
    }

}
