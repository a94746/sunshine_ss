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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class DataUtils {
    public static final LocalDate YO_20 = LocalDate.now().minusYears(20);
    public static final Faker FAKER = Faker.instance();

    public static Account newTypicalAccount(Location location) {
        return newTypicalAccount(5, 7, 2, location);
    }
    public static Account newTypicalAccount(int picsNum, int contactsNum, int devicesNum, Location location) {
        Account acc = AccountService.createCorrect();
        acc.setName(FAKER.name().firstName());
        acc.setBday(YO_20);
        acc.setDescription(FAKER.company().catchPhrase());
        acc.setLang(Language.EN);
        acc.setGender(Gender.MALE);
        acc.setMatchesNum(Gender.MALE.getMatchesNum());
        acc.setViews(FAKER.number().randomDigit());
        acc.setLikes(FAKER.number().randomDigit());
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

    private static int index = 0;
    public static Location newTypicalLocation() {
        Location location = new Location();
        location.setLastScheduling(LocalDateTime.now());
        location.setScheduledNow(false);
        location.setTimeShift((byte) FAKER.number().randomDigit());
        location.setNameRu(FAKER.address().cityName() + index++);
        location.setNameEn(FAKER.address().cityName() + index++);
        return location;
    }

    private static Filter newTypicalFilter(Account acc) {
        Filter filter = new Filter();
        filter.setAgeFrom((byte) (50 + FAKER.number().randomDigit()));
        filter.setAgeTo((byte) (50 - FAKER.number().randomDigit()));
        filter.setOwner(acc);
        filter.setChatPrefs(Set.of(ChatPref.ONLINE, ChatPref.LIFE));
        Set<RelationWithGenders> relationsWithGenders = Arrays.stream(Relation.values())
                .limit(3)
                .map(r -> {
                    RelationWithGenders relationWithGenders = new RelationWithGenders();
                    relationWithGenders.setFilter(filter);
                    relationWithGenders.setRelation(r);
                    relationWithGenders.setGenders(Set.of(Gender.MALE, Gender.NON_BINARY));
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
        cread.setEmail(FAKER.company().name());
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
}
