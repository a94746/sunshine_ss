package com.vindie.sunshine_ss.match;

import com.vindie.sunshine_ss.common.dto.UiKey;
import com.vindie.sunshine_ss.common.dto.exception.SunshineException;
import com.vindie.sunshine_ss.security.service.CurUserService;
import com.vindie.sunshine_ss.ui_dto.UiDailyMatch;
import com.vindie.sunshine_ss.ui_dto.UiLike;
import com.vindie.sunshine_ss.ui_dto.UiLikedMatch;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.vindie.sunshine_ss.match.MatchService.ACTUAL_MATCHES_DURATION;

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

    @GetMapping("/get_liked")
    public List<UiLikedMatch> getLiked() {
        return matchService.getLiked(CurUserService.get());
    }

    @PostMapping("/like")
    public boolean like(@RequestBody UiLike uiLike) {
        var curUser = CurUserService.get();
        if (matchService.getPair(uiLike.getPairId())
                .get(0)
                .getDate().isBefore(LocalDateTime.now().minus(ACTUAL_MATCHES_DURATION)))
            throw new SunshineException(UiKey.MATCH_NOT_AT_ACTUAL);
        return matchService.like(uiLike, curUser);
    }

}
