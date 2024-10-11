package com.vindie.sunshine_ss.match;

import com.vindie.sunshine_ss.common.service.AbstractController;
import com.vindie.sunshine_ss.ui_dto.UiDailyMatch;
import com.vindie.sunshine_ss.ui_dto.UiLike;
import com.vindie.sunshine_ss.ui_dto.UiLikedMatch;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/match")
public class MatchController extends AbstractController {
    private final MatchService matchService;

    @GetMapping("/get_daily")
    public List<UiDailyMatch> getDaily() {
        return matchService.getDaily(getCurrentUser());
    }

    @GetMapping("/get_liked")
    public List<UiLikedMatch> getLiked() {
        return matchService.getLiked(getCurrentUser());
    }

    @PostMapping("/like")
    public boolean like(@RequestBody UiLike uiLike) {
        return matchService.like(uiLike, getCurrentUser());
    }

}
