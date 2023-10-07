package com.vindie.sunshine_ss.account.controller;

import com.vindie.sunshine_ss.account.repo.ContactRepo;
import com.vindie.sunshine_ss.account.service.ContactService;
import com.vindie.sunshine_ss.common.dto.UiKey;
import com.vindie.sunshine_ss.common.dto.exception.SunshineException;
import com.vindie.sunshine_ss.common.record.UiContact;
import com.vindie.sunshine_ss.common.service.PropService;
import com.vindie.sunshine_ss.security.service.CurUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/contact")
public class ContactController {
    private final ContactRepo contactRepo;
    private final ContactService contactService;
    private final PropService propService;

    @PostMapping
    public long create(@RequestBody UiContact uiContact) {
        uiContact.validate2();
        var user = CurUserService.get();
        if (contactRepo.countByOwnerIdAndKey(user.getId(), uiContact.getKey()) > 0)
            throw new SunshineException(UiKey.NO_UNIQUE_CONTACT_KEY);
        if (contactRepo.countByOwnerId(user.getId()) >= propService.maxContacts)
            throw new SunshineException(UiKey.CONTACTS_MAXIMUM);
        return contactService.create(uiContact, user);
    }

    @PutMapping
    public void update(@RequestBody UiContact uiContact) {
        uiContact.validate();
        contactService.update(uiContact, CurUserService.get());
    }

    @DeleteMapping
    public void delete(@RequestBody Long id) {
        assertTrue(id != null, "ContactId == null");
        contactService.delete(id, CurUserService.get());
    }
}
