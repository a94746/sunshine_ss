package com.vindie.sunshine_ss.common;

import com.vindie.sunshine_ss.SunshineSsApplicationTests;
import com.vindie.sunshine_ss.common.email.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailServiceTest extends SunshineSsApplicationTests {
    @Autowired
    EmailService emailService;
    private static final List<String> VALID_EMAILS = List.of(
            "ds.vsVD@sd.Fv.dvF",
            "d@v.d",
            "123424234234234241@3234342234234.32424234234234");
    private static final List<String> NOT_VALID_EMAILS = List.of(
            "@www.www",
            "rrr@.ttt",
            "qqq@fff.",
            "fefsssse@dvsdvsv",
            "sdsdvssdvsvd.sdvsdv",
            "sdsdvssdvsvd.sdv@sdv");

    @Test
    void positive() throws Exception {
        VALID_EMAILS.forEach(e -> assertTrue(emailService.isValid(e)));
    }

    @Test
    void negative() throws Exception {
        NOT_VALID_EMAILS.forEach(e -> assertFalse(emailService.isValid(e)));
    }
}
