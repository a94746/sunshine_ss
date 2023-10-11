package com.vindie.sunshine_ss.base;

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
import com.vindie.sunshine_ss.pic.PicService;
import com.vindie.sunshine_ss.queue.repo.EventLineRepo;
import com.vindie.sunshine_ss.queue.repo.QueueElementRepo;
import com.vindie.sunshine_ss.utils.DataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.vindie.sunshine_ss.utils.DataUtils.getRandomElement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public abstract class WithData extends WithDb {
    public static final String PASS = "12345678";
    protected static final Random RANDOM = new Random();

    @Autowired
    protected DataUtils dataUtils;
    @Autowired
    protected AccountRepo accountRepo;
    @Autowired
    protected LocationRepo locationRepo;
    @Autowired
    protected FilterRepo filterRepo;
    @Autowired
    protected PicService picService;
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
    protected void createData() {
        final int locationsNum = 2;
        final int accountsNum = 83;
        final int matchPairsNum = 10;

        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < locationsNum; i++) {
            locations.add(dataUtils.newTypicalLocation());
        }
        locationRepo.saveAll(locations);
        assertEquals(locationsNum, locationRepo.findAll().size());

        List<Account> accounts = new ArrayList<>();
        var theFirstAcc = dataUtils.newTypicalAccount(getRandomElement(locations), true);
        accounts.add(theFirstAcc);
        for (int i = 0; i < accountsNum - 1; i++) {
            Location location = getRandomElement(locations);
            accounts.add(dataUtils.newTypicalAccount(location, false));
        }
        accountRepo.saveAll(accounts);
        assertEquals(accountsNum, accountRepo.findAll().size());
        assertEquals(accounts.stream().filter(a -> a.getFilter() != null).count(), filterRepo.findAll().size());
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
                .count(), picService.findAll().size());
        assertEquals(accounts.stream()
                .filter(a -> a.getFilter() != null)
                .map(Account::getFilter)
                .map(Filter::getRelationsWithGenders)
                .flatMap(Collection::stream)
                .count(), relationWithGendersRepo.findAll().size());
        assertEquals(accounts.stream()
                .filter(a -> a.getFilter() != null)
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
        String pairId = UUID.randomUUID().toString();
        matches.add(dataUtils.newTypicalMatch(accounts.get(0), accounts.get(1), pairId));
        matches.add(dataUtils.newTypicalMatch(accounts.get(1), accounts.get(0), pairId));
        for (int i = 1; i < matchPairsNum; i++) {
            var acc1 = accounts.get(i);
            if (acc1.getFilter() == null)
                continue;
            var var2 = RANDOM.nextInt(4);
            for (int i2 = 0; i2 < var2; i2++) {
                var acc2 = getRandomElement(accounts);
                if (acc1.equals(acc2))
                    continue;
                if (acc2.getFilter() == null )
                    continue;
                if (matches.stream().anyMatch(m -> m.getOwner().equals(acc1) && m.getPartner().equals(acc2)))
                    continue;
                String pairId2 = UUID.randomUUID().toString();
                matches.add(dataUtils.newTypicalMatch(acc1, acc2, pairId2));
                matches.add(dataUtils.newTypicalMatch(acc2, acc1, pairId2));
            }
        }
        matchRepo.saveAll(matches);
        assertNotEquals(0, matchRepo.findAll().size());
        assertEquals(accountsNum, accountRepo.findAll().size());
        account = accountRepo.findWithCreadAndDevices().stream()
                .filter(a -> a.getName().equals(theFirstAcc.getName()) && a.getCread().getEmail().equals(theFirstAcc.getCread().getEmail()))
                .findFirst()
                .orElseThrow();
    }
}
