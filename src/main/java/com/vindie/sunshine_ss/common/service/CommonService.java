package com.vindie.sunshine_ss.common.service;

import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.common.dto.ChatPref;
import com.vindie.sunshine_ss.common.service.properties.PropertiesService;
import com.vindie.sunshine_ss.location.LocationRepo;
import com.vindie.sunshine_ss.queue.repo.QueueElementRepo;
import com.vindie.sunshine_ss.security.record.User;
import com.vindie.sunshine_ss.ui_dto.UiLocation;
import com.vindie.sunshine_ss.ui_dto.UiLoginOpeningDialog;
import com.vindie.sunshine_ss.ui_dto.UiSettings;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CommonService {
    private final PropertiesService properties;
    private final QueueElementRepo queueElementRepo;
    private final LocationRepo locationRepo;
    private final AccountRepo accountRepo;

    public List<UiLocation> getLocations() {
        return locationRepo.findAll()
                .stream()
                .map(l -> UiLocation.builder()
                        .id(l.getId())
                        .name(l.getName())
                        .build())
                .toList();
    }

    public LocalDateTime getLastScheduling(User user) {
        return accountRepo.getLastScheduling(user.getId());
    }

    public List<ChatPref> getChatPref() {
        return Arrays.asList(ChatPref.values());
    }

    public UiSettings gerSettings() {
        var uiSettings = new UiSettings();
        uiSettings.setPicCacheTTL(properties.ttl.uiPicCacheTTL);
        uiSettings.setMatchesFrequency(properties.matchesFrequency);
        return uiSettings;
    }

    @Transactional
    public List<UiLoginOpeningDialog> gerUiLoginOpeningDialogs(Long userId) {
        var notifs = queueElementRepo.findOpeningDialogsByOwner(userId);
        queueElementRepo.deleteOpeningDialogsByOwner(userId);
        return notifs;
    }
}
