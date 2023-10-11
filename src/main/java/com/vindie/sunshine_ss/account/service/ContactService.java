package com.vindie.sunshine_ss.account.service;

import com.vindie.sunshine_ss.account.dto.Contact;
import com.vindie.sunshine_ss.account.repo.AccountRepo;
import com.vindie.sunshine_ss.account.repo.ContactRepo;
import com.vindie.sunshine_ss.security.record.User;
import com.vindie.sunshine_ss.ui_dto.UiContact;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class ContactService {
    private ContactRepo contactRepo;
    private AccountRepo accountRepo;

    public long create(UiContact uiContact, User user) {
        var contact = new Contact();
        contact.setKey(uiContact.getKey());
        contact.setValue(uiContact.getValue());
        var account = accountRepo.getReferenceById(user.getId());
        contact.setOwner(account);
        return contactRepo.save(contact).getId();
    }

    @Transactional
    public void update(UiContact uiContact, User user) {
        var contact = contactRepo.findByIdAndOwnerId(uiContact.getId(), user.getId())
                .orElseThrow();
        contact.setKey(uiContact.getKey());
        contact.setValue(uiContact.getValue());
        contactRepo.save(contact);
    }

    @Transactional
    public void delete(Long id, User user) {
        contactRepo.deleteByIdAndOwnerId(id, user.getId());
    }

}
