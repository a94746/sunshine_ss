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
import com.vindie.sunshine_ss.filter.dto.Filter;
import com.vindie.sunshine_ss.filter.dto.RelationWithGenders;
import com.vindie.sunshine_ss.location.Location;
import com.vindie.sunshine_ss.match.Match;
import com.vindie.sunshine_ss.match.MatchService;
import com.vindie.sunshine_ss.pic.Pic;
import com.vindie.sunshine_ss.queue.dto.EventLine;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class DataUtils {
    public static final Faker FAKER = Faker.instance();
    public static final Random RANDOM = new Random();
    private static int index = 0;

    public static Account newTypicalAccount(Location location) {
        return newTypicalAccount(RANDOM.nextInt(5), RANDOM.nextInt(7), RANDOM.nextInt(1) + 1, location);
    }
    public static Account newTypicalAccount(int picsNum, int contactsNum, int devicesNum, Location location) {
        Account acc = AccountService.createCorrect();
        acc.setName(FAKER.name().firstName());
        acc.setBday(LocalDate.now()
                .minusYears(18)
                .minusYears(RANDOM.nextInt(60))
                .minusDays(RANDOM.nextInt(365)));
        acc.setDescription(FAKER.company().catchPhrase());
        acc.setLang(Language.EN);
        acc.setRating((byte) (1 + RANDOM.nextInt(99)));

        boolean prem = RANDOM.nextInt(10) < 3;
        Gender gender = getRandomElement(Gender.values());
        acc.setGender(gender);
        acc.setMatchesNum(gender.getMatchesNum());
        acc.setPremMatchesNum(prem
                ? gender.getPremMatchesNum()
                : null);
        acc.setViews(RANDOM.nextInt(100) );
        acc.setLikes(acc.getViews() == 0 ? 0 : acc.getViews() - RANDOM.nextInt(acc.getViews()));
        acc.setLastPresence(LocalDateTime.now());
        acc.setLocation(location);

        Filter filter = newTypicalFilter(acc);
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

    public static Location newTypicalLocation() {
        Location location = new Location();
        location.setLastScheduling(LocalDateTime.now());
        location.setTimeShift((byte) FAKER.number().randomDigit());
        location.setName(FAKER.address().cityName() + index++);
        return location;
    }

    private static Filter newTypicalFilter(Account acc) {
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

    private static Pic newTypicalPic(Account owner) {
        Pic pic = new Pic();
        pic.setFile(FAKER.file().fileName().getBytes(StandardCharsets.UTF_8));
        pic.setNum((byte) FAKER.number().randomDigit());
        pic.setOwner(owner);
        return pic;
    }

    private static Contact newTypicalContact(Account owner) {
        Contact contact = new Contact();
        contact.setKey(FAKER.app().name());
        contact.setValue(FAKER.app().author());
        contact.setOwner(owner);
        return contact;
    }
    private static Device newTypicalDevice(Account owner) {
        Device device = new Device();
        device.setAndroidId(FAKER.app().version());
        device.setImei(null);
        device.setWifiMac(FAKER.app().version());
        device.setPhoneNum(FAKER.phoneNumber().phoneNumber());
        device.setAppVersion(FAKER.app().version());
        device.setOwner(owner);
        return device;
    }

    private static Cread newTypicalCread(Account owner) {
        Cread cread = new Cread();
        cread.setEmail(FAKER.company().name() + index++);
        cread.setPass("12345678");
        cread.setEmailCode(null);
        cread.setOwner(owner);
        return cread;
    }

    public static Match newTypicalMatch(Account owner, Account partner) {
        Match match = MatchService.createCorrect();
        match.setOwner(owner);
        match.setPartner(partner);
        match.setMessage(FAKER.ancient().titan());
        return match;
    }

    public static EventLine newTypicalEventLine(Long ownerId, Long locationId, Boolean notification, Boolean openingDialog) {
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
