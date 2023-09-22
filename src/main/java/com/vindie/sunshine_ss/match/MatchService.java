package com.vindie.sunshine_ss.match;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class MatchService {
    private MatchRepo matchRepo;

    public static Match createCorrect() {
        Match match = new Match();
        match.setLiked(false);
        match.setViewed(false);
        return match;
    }

}
