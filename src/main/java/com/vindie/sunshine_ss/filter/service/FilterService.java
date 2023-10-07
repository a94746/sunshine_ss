package com.vindie.sunshine_ss.filter.service;

import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.common.dto.Relation;
import com.vindie.sunshine_ss.common.record.UiMyFilter;
import com.vindie.sunshine_ss.filter.dto.Filter;
import com.vindie.sunshine_ss.filter.dto.RelationWithGenders;
import com.vindie.sunshine_ss.filter.repo.FilterRepo;
import com.vindie.sunshine_ss.security.record.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FilterService {
    private FilterRepo filterRepo;
    private AccountRepo accountRepo;

    @Transactional
    public void editMy(UiMyFilter ui, User user) {
        Filter filter;
        var filterOpt = filterRepo.findByOwnerIdWithEverything(user.getId());
        if (filterOpt.isPresent()) {
            filter = filterOpt.get();
        } else {
            filter = new Filter();
            filter.setOwner(accountRepo.getReferenceById(user.getId()));
        }

        filter.setAgeFrom(ui.getAgeFrom());
        filter.setAgeTo(ui.getAgeTo());
        filter.setChatPrefs(ui.getChatPrefs()
                .entrySet()
                .stream()
                .filter(e -> Boolean.TRUE.equals(e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet()));

        for (Relation relation : Relation.values()) {
            var genders = ui.getRelationsWithGenders().get(relation) == null
                    ? null
                    : ui.getRelationsWithGenders().get(relation).entrySet()
                    .stream()
                    .filter(e -> Boolean.TRUE.equals(e.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
            var optR2gs = filter.getRelationsWithGenders().stream()
                    .filter(r2gs -> r2gs.getRelation() == relation)
                    .findFirst();

            if (CollectionUtils.isEmpty(genders) && optR2gs.isPresent()) {
                filter.getRelationsWithGenders().remove(optR2gs.get());
            } else {
                if (optR2gs.isPresent()) {
                    optR2gs.get().setGenders(genders);
                } else {
                    var v1 = new RelationWithGenders();
                    v1.setRelation(relation);
                    v1.setFilter(filter);
                    v1.setGenders(genders);
                    filter.getRelationsWithGenders().add(v1);
                }
            }
        }
        filterRepo.save(filter);
    }
}
