package com.vindie.sunshine_ss.interfaces;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.account.repo.ContactRepo;
import com.vindie.sunshine_ss.account.repo.CreadRepo;
import com.vindie.sunshine_ss.account.repo.DeviceRepo;
import com.vindie.sunshine_ss.filter.dto.Filter;
import com.vindie.sunshine_ss.filter.dto.RelationWithGenders;
import com.vindie.sunshine_ss.filter.repo.FilterRepo;
import com.vindie.sunshine_ss.filter.repo.RelationWithGendersRepo;
import com.vindie.sunshine_ss.location.Location;
import com.vindie.sunshine_ss.location.LocationRepo;
import com.vindie.sunshine_ss.match.Match;
import com.vindie.sunshine_ss.match.MatchRepo;
import com.vindie.sunshine_ss.pic.PicRepo;
import com.vindie.sunshine_ss.queue.repo.EventLineRepo;
import com.vindie.sunshine_ss.queue.repo.QueueElementRepo;
import com.vindie.sunshine_ss.utils.DataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class WithDbData extends WithSqlContener {
    protected static final Random RANDOM = new Random();

    @Autowired
    protected AccountRepo accountRepo;
    @Autowired
    protected LocationRepo locationRepo;
    @Autowired
    protected FilterRepo filterRepo;
    @Autowired
    protected PicRepo picRepo;
    @Autowired
    protected ContactRepo contactRepo;
    @Autowired
    protected DeviceRepo deviceRepo;
    @Autowired
    protected CreadRepo creadRepo;
    @Autowired
    protected RelationWithGendersRepo relationWithGendersRepo;
    @Autowired
    protected MatchRepo matchRepo;
    @Autowired
    protected EventLineRepo eventLineRepo;
    @Autowired
    protected QueueElementRepo queueElementRepo;

    protected Account account;

    @BeforeEach
    public void createData() {
        final int locationsNum = 2;
        final int accountsNum = 63;
        final int matchPairsNum = 6;

        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < locationsNum; i++) {
            locations.add(DataUtils.newTypicalLocation());
        }
        locationRepo.saveAll(locations);
        assertEquals(locationsNum, locationRepo.findAll().size());

        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < accountsNum; i++) {
            Location location = DataUtils.getRandomElement(locations);
            accounts.add(DataUtils.newTypicalAccount(location));
        }
        accountRepo.saveAll(accounts);
        assertEquals(accountsNum, accountRepo.findAll().size());
        assertEquals(accountsNum, filterRepo.findAll().size());
        assertEquals(accountsNum, creadRepo.findAll().size());
        assertEquals(accounts.stream()
                .map(Account::getContacts)
                .flatMap(Collection::stream)
                .count(), contactRepo.findAll().size());
        assertEquals(accounts.stream()
                .map(Account::getDevices)
                .flatMap(Collection::stream)
                .count(), deviceRepo.findAll().size());
        assertEquals(accounts.stream()
                .map(Account::getPics)
                .flatMap(Collection::stream)
                .count(), picRepo.findAll().size());
        assertEquals(accounts.stream()
                .map(Account::getFilter)
                .map(Filter::getRelationsWithGenders)
                .flatMap(Collection::stream)
                .count(), relationWithGendersRepo.findAll().size());
        assertEquals(accounts.stream()
                .map(Account::getFilter)
                .map(Filter::getRelationsWithGenders)
                .flatMap(Collection::stream)
                .map(RelationWithGenders::getGenders)
                .flatMap(Collection::stream)
                .count(),
                relationWithGendersRepo.findAll().stream()
                        .map(RelationWithGenders::getGenders)
                        .flatMap(Collection::stream)
                        .count());

        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < matchPairsNum; i++) {
            matches.add(DataUtils.newTypicalMatch(accounts.get(i), accounts.get(i+1)));
            matches.add(DataUtils.newTypicalMatch(accounts.get(i+1), accounts.get(i)));
        }
        matchRepo.saveAll(matches);
        assertEquals(matchPairsNum * 2, matchRepo.findAll().size());
        assertEquals(accountsNum, accountRepo.findAll().size());
        account = accountRepo.findAll()
                .stream()
                .findFirst()
                .orElseThrow();
    }
}
