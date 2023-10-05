package com.vindie.sunshine_ss.account.service;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.account.repo.CreadRepo;
import com.vindie.sunshine_ss.account.repo.DeviceRepo;
import com.vindie.sunshine_ss.common.dto.ChatPref;
import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Relation;
import com.vindie.sunshine_ss.common.record.UiMyAccount;
import com.vindie.sunshine_ss.common.record.UiMyFilter;
import com.vindie.sunshine_ss.common.record.UserInfo;
import com.vindie.sunshine_ss.filter.dto.RelationWithGenders;
import com.vindie.sunshine_ss.match.MatchService;
import com.vindie.sunshine_ss.pic.PicService;
import com.vindie.sunshine_ss.security.record.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ContactService contactService;
    private CreadService creadService;
    private DeviceService deviceService;
    private MatchService matchService;

    public UiMyAccount getMyAccount(User user) {
        var acc = accountRepo.findForMyAccount(user.getId()).orElseThrow();
        var prem = isPrem(acc);

        UiMyAccount uiMyAccount = new UiMyAccount();
        uiMyAccount.setEmail(acc.getCread().getEmail());
        uiMyAccount.setName(acc.getName());
        uiMyAccount.setDescription(acc.getDescription());
        uiMyAccount.setGender(acc.getGender());
        uiMyAccount.setBday(acc.getBday());
        uiMyAccount.setLocationName(acc.getLocation().getName());
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
        acc.getContacts().forEach(c -> uiMyAccount.getContacts().put(c.getKey(), c.getValue()));
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

    public List<Account> findForScheduling(Long locationId) {
        return accountRepo.findForScheduling(locationId);
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
