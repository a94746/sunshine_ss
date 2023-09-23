package com.vindie.sunshine_ss.match;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class MatchService {
    private MatchRepo matchRepo;

    public Map<Long, List<Long>> findAllByOwnerIds(List<Long> ownerIds) {
        Map<Long, List<Long>> result = new HashMap<>();
        matchRepo.findAllByOwnerIds(ownerIds)
                .forEach(match ->
                        result.computeIfAbsent(match.getOwner().getId(), irnore -> new ArrayList<>())
                                .add(match.getPartner().getId()));

    }

    public static Match createCorrect() {
        Match match = new Match();
        match.setLiked(false);
        match.setViewed(false);
        return match;
    }

}
