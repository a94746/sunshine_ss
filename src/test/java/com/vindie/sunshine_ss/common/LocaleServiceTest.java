package com.vindie.sunshine_ss.common;

import com.vindie.sunshine_ss.base.WithDb;
import com.vindie.sunshine_ss.common.dto.Language;
import com.vindie.sunshine_ss.common.service.LocaleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocaleServiceTest extends WithDb {
    @Autowired
    LocaleService localeService;

    @Test
    void locale_test() {
        assertEquals("test", localeService.localize("test.value", Language.EN));
        assertEquals("тест", localeService.localize("test.value", Language.RU));
    }
}
