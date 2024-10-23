package com.vindie.sunshine_ss.utils;

import com.github.javafaker.Faker;
import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.account.dto.Contact;
import com.vindie.sunshine_ss.account.dto.Cread;
import com.vindie.sunshine_ss.account.dto.Device;
import com.vindie.sunshine_ss.account.service.AccountService;
import com.vindie.sunshine_ss.common.dto.ChatPref;
import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Language;
import com.vindie.sunshine_ss.common.dto.Relation;
import com.vindie.sunshine_ss.common.service.properties.PropertiesService;
import com.vindie.sunshine_ss.filter.dto.Filter;
import com.vindie.sunshine_ss.filter.dto.RelationWithGenders;
import com.vindie.sunshine_ss.location.Location;
import com.vindie.sunshine_ss.match.Match;
import com.vindie.sunshine_ss.match.MatchService;
import com.vindie.sunshine_ss.pic.Pic;
import com.vindie.sunshine_ss.queue.dto.EventLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.vindie.sunshine_ss.base.WithData.PASS;

@Service
public class DataUtils {
    public static final Faker FAKER = Faker.instance();
    public static final Random RANDOM = new Random();
    public static int index = 0;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PropertiesService propertiesService;

    public Account newTypicalAccount(Location location, boolean filterNeeded) {
        return newTypicalAccount(RANDOM.nextInt(5), RANDOM.nextInt(7), RANDOM.nextInt(1) + 1, location, filterNeeded);
    }
    public Account newTypicalAccount(int picsNum, int contactsNum, int devicesNum, Location location, boolean filterNeeded) {
        Account acc = AccountService.createCorrect();
        acc.setName(FAKER.name().firstName());
        acc.setBday(LocalDate.now()
                .minusYears(18)
                .minusYears(RANDOM.nextInt(60))
                .minusDays(RANDOM.nextInt(365)));
        acc.setBdayLastChange(LocalDate.now().minusDays(RANDOM.nextInt(730)));
        acc.setDescription(FAKER.company().catchPhrase());
        acc.setLang(Language.EN);
        acc.setViews(RANDOM.nextInt(100) );
        acc.setLikes(acc.getViews() == 0 ? 0 : acc.getViews() - RANDOM.nextInt(acc.getViews()));
        acc.setRating(acc.getLikes() >= 3
                ? BigDecimal.valueOf(RANDOM.nextDouble(99))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue()
                : 50);

        boolean prem = RANDOM.nextInt(10) < 3;
        Gender gender = getRandomElement(Gender.values());
        acc.setGender(gender);
        acc.setMatchesNum(propertiesService.genderMatchNum.getMatchMaxNum(gender, false));
        acc.setPremMatchesNum(prem
                ? propertiesService.genderMatchNum.getMatchMaxNum(gender, true)
                : null);
        acc.setPremTill(prem
                ? LocalDateTime.now().plusHours(1)
                : null);
        acc.setLastPresence(LocalDateTime.now());
        acc.setLocation(location);
        acc.setLocationLastChange(LocalDateTime.now().minusHours(RANDOM.nextInt(48)));

        Filter filter = filterNeeded || getRandomElement(List.of(Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE))
                ? newTypicalFilter(acc)
                : null;
        acc.setFilter(filter);
        acc.setCread(newTypicalCread(acc));

        List<Pic> pics = new ArrayList<>();
        for (int i = 0; i < picsNum; i++) {
            pics.add(newTypicalPic(acc));
        }
        acc.setPics(pics);

        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < contactsNum; i++) {
            contacts.add(newTypicalContact(acc));
        }
        acc.setContacts(contacts);

        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < devicesNum; i++) {
            devices.add(newTypicalDevice(acc));
        }
        acc.setDevices(devices);

        return acc;
    }

    public Location newTypicalLocation() {
        Location location = new Location();
        location.setLastScheduling(LocalDateTime.now());
        location.setTimeShift((byte) FAKER.number().randomDigit());
        location.setName(FAKER.address().cityName() + index++);
        return location;
    }

    private Filter newTypicalFilter(Account acc) {
        Filter filter = new Filter();
        filter.setAgeFrom((byte) (35 - RANDOM.nextInt(17)));
        filter.setAgeTo((byte) (35 + RANDOM.nextInt(30)));
        filter.setOwner(acc);
        filter.setChatPrefs(new HashSet<>(getRandomPart(ChatPref.values())));
        Set<RelationWithGenders> relationsWithGenders = getRandomPart(Relation.values())
                .stream()
                .map(r -> {
                    RelationWithGenders relationWithGenders = new RelationWithGenders();
                    relationWithGenders.setFilter(filter);
                    relationWithGenders.setRelation(r);
                    relationWithGenders.setGenders(new HashSet<>(getRandomPart(Gender.values())));
                    return relationWithGenders;
                })
                .collect(Collectors.toSet());

        filter.setRelationsWithGenders(relationsWithGenders);
        return filter;
    }

    public Pic newTypicalPic(Account owner) {
        Pic pic = new Pic();
        pic.setFile(FAKER.file().fileName().getBytes(StandardCharsets.UTF_8));
        pic.setOwner(owner);
        return pic;
    }

    private Contact newTypicalContact(Account owner) {
        Contact contact = new Contact();
        contact.setKey(FAKER.app().name());
        contact.setValue(FAKER.app().author());
        contact.setOwner(owner);
        return contact;
    }
    private Device newTypicalDevice(Account owner) {
        Device device = new Device();
        device.setUniqueId(FAKER.app().version() + index++);
        device.setImei(null);
        device.setWifiMac(FAKER.app().version());
        device.setPhoneNum(FAKER.phoneNumber().phoneNumber());
        device.setAppVersion(FAKER.app().version());
        device.setOwner(owner);
        return device;
    }

    private Cread newTypicalCread(Account owner) {
        Cread cread = new Cread();
        cread.setEmail(FAKER.internet().safeEmailAddress(owner.getName()+ index++));
        cread.setPass(passwordEncoder.encode(PASS));
        cread.setOwner(owner);
        return cread;
    }

    public Match newTypicalMatch(Account owner, Account partner, String pairId) {
        Match match = MatchService.createCorrect();
        match.setPairId(pairId);
        match.setOwner(owner);
        match.setPartner(partner);
        match.setMessage(FAKER.ancient().titan());
        match.setLiked(RANDOM.nextInt(2) == 1);
        return match;
    }

    public EventLine newTypicalEventLine(Long ownerId, Long locationId, Boolean notification, Boolean openingDialog) {
        EventLine ev = new EventLine();
        ev.setTitle(FAKER.company().catchPhrase());
        ev.setText("Text: " + FAKER.company().catchPhrase());
        ev.setProcessed(false);
        ev.setNotification(notification);
        ev.setOpeningDialog(openingDialog);
        ev.setOwnerId(ownerId);
        ev.setLocationId(locationId);
        return ev;
    }

    public static <T> T getRandomElement(List<T> input) {
        return input.get(RANDOM.nextInt(input.size()));
    }

    public static <T> T getRandomElement(T[] input) {
        return getRandomElement(Arrays.asList(input));
    }

    public static <T> List<T> getRandomPart(List<T> input) {
        final int resultSize = RANDOM.nextInt(input.size());
        Set<Integer> ids = new HashSet<>();
        while (ids.size() < resultSize) {
            ids.add(RANDOM.nextInt(input.size()));
        }
        return ids.stream()
                .map(input::get)
                .toList();
    }
    public static <T> List<T> getRandomPart(T[] input) {
        return getRandomPart(Arrays.asList(input));
    }
}
