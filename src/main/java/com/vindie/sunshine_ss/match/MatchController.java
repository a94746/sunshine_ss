package com.vindie.sunshine_ss.match;

import com.vindie.sunshine_ss.match.record.UiDailyMatch;
import com.vindie.sunshine_ss.security.service.CurUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/match")
public class MatchController {
    private final MatchService matchService;

    @GetMapping("/get_daily")
    public List<UiDailyMatch> getDaily() {
        return matchService.getDaily(CurUserService.get());
    }

}
