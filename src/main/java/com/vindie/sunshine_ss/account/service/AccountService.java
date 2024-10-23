package com.vindie.sunshine_ss.account.service;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.account.repo.CreadRepo;
import com.vindie.sunshine_ss.account.repo.DeviceRepo;
import com.vindie.sunshine_ss.common.dto.ChatPref;
import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Relation;
import com.vindie.sunshine_ss.common.dto.UiKey;
import com.vindie.sunshine_ss.common.dto.exception.SunshineException;
import com.vindie.sunshine_ss.common.service.properties.PropertiesService;
import com.vindie.sunshine_ss.filter.dto.RelationWithGenders;
import com.vindie.sunshine_ss.location.LocationRepo;
import com.vindie.sunshine_ss.pic.PicService;
import com.vindie.sunshine_ss.security.record.User;
import com.vindie.sunshine_ss.ui_dto.UiContact;
import com.vindie.sunshine_ss.ui_dto.UiMyAccount;
import com.vindie.sunshine_ss.ui_dto.UiMyFilter;
import com.vindie.sunshine_ss.ui_dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {
    private AccountRepo accountRepo;
    private PicService picService;
    private DeviceRepo deviceRepo;
    private CreadRepo creadRepo;
    private PropertiesService properties;
    private LocationRepo locationRepo;

    @Transactional(noRollbackFor = SunshineException.class)
    public void editMy(UiMyAccount ui, User user) {
        var acc = accountRepo.findById(user.getId()).orElseThrow();
        acc.setName(ui.getName().trim());
        acc.setDescription(ui.getDescription().trim());

        if (!acc.getBday().isEqual(ui.getBday())) {
            if (acc.getBdayLastChange().isBefore(LocalDate.now().minus(properties.allowedFrequencyOfBdayChange))) {
                acc.setBday(ui.getBday());
                acc.setBdayLastChange(LocalDate.now());
            } else {
                throw new SunshineException(UiKey.CANT_CHANGE_BDAY_SO_OFTEN);
            }
        }
        if (!acc.getLocation().getId().equals(ui.getLocationId())) {
            if (acc.getLocationLastChange().isBefore(LocalDateTime.now().minus(properties.locationLastChange))) {
                acc.setLocation(locationRepo.getReferenceById(ui.getLocationId()));
                acc.setLocationLastChange(LocalDateTime.now());
            } else {
                throw new SunshineException(UiKey.CANT_CHANGE_LOCATION_SO_OFTEN);
            }
        }
        accountRepo.save(acc);
    }

    public UiMyAccount getMyAccount(User user) {
        var acc = accountRepo.findForMyAccount(user.getId()).orElseThrow();
        var prem = isPrem(acc);

        UiMyAccount uiMyAccount = new UiMyAccount();
        uiMyAccount.setEmail(acc.getCread().getEmail());
        uiMyAccount.setName(acc.getName());
        uiMyAccount.setDescription(acc.getDescription());
        uiMyAccount.setGender(acc.getGender());
        uiMyAccount.setBday(acc.getBday());
        uiMyAccount.setLocationId(acc.getLocation().getId());
        uiMyAccount.setRating(prem ? acc.getRating() : null);
        uiMyAccount.setActualMatchesNum(prem ? acc.getPremMatchesNum() : acc.getMatchesNum());
        uiMyAccount.setPrem(prem);

        UiMyFilter uiMyFilter = new UiMyFilter();
        uiMyFilter.setAgeFrom(acc.getFilter().getAgeFrom());
        uiMyFilter.setAgeTo(acc.getFilter().getAgeTo());
        for (Relation relation : Relation.values()) {
            Map<Gender, Boolean> genders = new EnumMap<>(Gender.class);
            for (Gender gender : Gender.values()) {
                boolean active = acc.getFilter().getRelationsWithGenders()
                        .stream()
                        .filter(rWg -> rWg.getRelation() == relation)
                        .map(RelationWithGenders::getGenders)
                        .flatMap(Collection::stream)
                        .anyMatch(gender::equals);
                genders.put(gender, active);
            }
            uiMyFilter.getRelationsWithGenders().put(relation, genders);
        }
        for (ChatPref chatPref : ChatPref.values()) {
            boolean active = acc.getFilter().getChatPrefs().contains(chatPref);
            uiMyFilter.getChatPrefs().put(chatPref, active);
        }
        uiMyAccount.setFilter(uiMyFilter);
        acc.getContacts().forEach(c -> {
            var uiContact = UiContact.builder()
                    .id(c.getId())
                    .key(c.getKey())
                    .value(c.getValue())
                    .build();
            uiMyAccount.getContacts().add(uiContact);
        });
        uiMyAccount.getPicInfos().addAll(picService.getPicInfosByOwnerId(acc.getId()));
        return uiMyAccount;
    }

    @Transactional
    public void takeInfo(UserInfo info, User user) {
        deviceRepo.takeInfo(info.getUniqueId(), info.getAppVersion());
        accountRepo.takeInfo(user.getId(), info.getLang(), LocalDateTime.now());
    }

    @Transactional
    public void changeEmail(String newEmail, User user) {
        creadRepo.changeEmail(newEmail, user.getId());
    }

    @Transactional
    public void softDelete(Long id) {
        accountRepo.softDelete(id);
    }

    public List<Account> findForScheduling(Long locationId) {
        return accountRepo.findForScheduling(locationId);
    }

    public Account getReferenceById(Long accountId) {
        return accountRepo.getReferenceById(accountId);
    }

    public int countByLocationId(Long locationId) {
        return accountRepo.countByLocationId(locationId);
    }

    public void incrementViews(Collection<Long> ids) {
        accountRepo.incrementViews(ids);
    }

    public static boolean isPrem(Account acc) {
        return acc.getPremTill() != null
                && acc.getPremTill().isAfter(LocalDateTime.now());
    }

    public static Account createCorrect() {
        Account acc = new Account();
        acc.setViews(0);
        acc.setLikes(0);
        acc.setRating(50D);
        acc.setDeleted(false);
        return acc;
    }

}
