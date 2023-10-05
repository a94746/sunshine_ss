package com.vindie.sunshine_ss.match;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vindie.sunshine_ss.base.WithMvc;
import com.vindie.sunshine_ss.match.record.UiDailyMatch;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static com.vindie.sunshine_ss.account.service.AccountService.isPrem;
import static com.vindie.sunshine_ss.security.config.RequestFilter.MY_HEADER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MatchControllerTest extends WithMvc {

    @Test
    void get_daily_test() throws Exception {
        var acc2 = accountRepo.findAll()
                .stream()
                .filter(a -> a.getFilter() != null && !a.getId().equals(account.getId()))
                .findAny()
                .orElseThrow();
        matchRepo.save(dataUtils.newTypicalMatch(account, acc2));
        mvc.perform(get("/match/get_daily")
                        .header(HttpHeaders.AUTHORIZATION, getJwtHeader())
                        .header(MY_HEADER_NAME, myHeaderCode))
                .andExpect(status().isOk())
                .andExpect(modelMatches(new TypeReference<List<UiDailyMatch>>() {}, l -> {
                    var allMathces = matchRepo.findAll();
                        assertEquals(allMathces.stream()
                                .filter(m -> m.getOwner().getId().equals(account.getId()))
                                .count(),
                                l.size());
                        l.forEach(m -> {
                            var mFr = matchRepo.findAllByOwnerId(account.getId()).stream()
                                    .filter(m2 -> m2.getPartner().getId().equals(m.getId()))
                                    .findFirst()
                                    .orElseThrow();
                            assertEquals(mFr.getPartner().getId(), m.getId());
                            assertEquals(mFr.getPartner().getName(), m.getName());
                            assertEquals(Period.between(mFr.getPartner().getBday(), LocalDate.now()).getYears(), m.getAge());
                            assertEquals(mFr.getPartner().getDescription(), m.getDescription());
                            assertEquals(mFr.getPartner().getGender(), m.getGender());
                            assertEquals(isPrem(account) ? mFr.getLiked() : null, m.getLikedYou());
                            assertEquals(isPrem(mFr.getPartner()) ? mFr.getMessage() : null, m.getMessage());
                            assertEquals(picService.getPicInfosByOwnerId(mFr.getPartner().getId()).size(), m.getPicInfos().size());
                        });
                }));
    }

}
