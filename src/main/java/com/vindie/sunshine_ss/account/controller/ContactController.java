package com.vindie.sunshine_ss.account.controller;

import com.vindie.sunshine_ss.account.repo.ContactRepo;
import com.vindie.sunshine_ss.account.service.ContactService;
import com.vindie.sunshine_ss.common.dto.UiKey;
import com.vindie.sunshine_ss.common.dto.exception.SunshineException;
import com.vindie.sunshine_ss.common.service.AbstractController;
import com.vindie.sunshine_ss.common.service.PropertiesService;
import com.vindie.sunshine_ss.ui_dto.UiContact;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/contact")
public class ContactController extends AbstractController {
    private final ContactRepo contactRepo;
    private final ContactService contactService;
    private final PropertiesService properties;

    @PostMapping
    public long create(@RequestBody UiContact uiContact) {
        validateCreateRequest(uiContact);
        return contactService.create(uiContact, getCurrentUser());
    }

    @PutMapping
    public void update(@RequestBody UiContact uiContact) {
        uiContact.validate();
        contactService.update(uiContact, getCurrentUser());
    }

    @DeleteMapping
    public void delete(@RequestBody Long id) {
        assertTrue(id != null, "ContactId == null");
        contactService.delete(id, getCurrentUser());
    }

    private void validateCreateRequest(UiContact uiContact) {
        uiContact.validate2();
        if (contactRepo.countByOwnerIdAndKey(getCurrentUserId(), uiContact.getKey()) > 0)
            throw new SunshineException(UiKey.NO_UNIQUE_CONTACT_KEY);
        if (contactRepo.countByOwnerId(getCurrentUserId()) >= properties.maxContactsPerAccount)
            throw new SunshineException(UiKey.CONTACTS_MAXIMUM);
    }
}
