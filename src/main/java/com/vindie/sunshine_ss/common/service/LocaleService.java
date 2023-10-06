package com.vindie.sunshine_ss.common.service;

import com.google.common.collect.Maps;
import com.vindie.sunshine_ss.common.dto.Language;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LocaleService {

    private final Map<Language, Map<String, String>> langTables;

    public LocaleService() {
        this.langTables = fillLangTable();
    }

    public String localize(String key, Language language, String... params) {
        Map<String, String> langTable = langTables.getOrDefault(language, langTables.get(Language.EN));
        String tableValue = langTable.getOrDefault(key, langTables.get(Language.EN).get(key));

        String translationValue;
        if (tableValue == null) {
            translationValue = key;
        } else {
            if (params != null && params.length > 0) {
                translationValue = MessageFormat.format(tableValue, (Object[]) params);
            } else {
                translationValue = tableValue;
            }
        }
        return translationValue.trim();
    }

    private Map<Language, Map<String, String>> fillLangTable() {
        return Arrays.stream(Language.values())
                .map(lang -> Pair.of(lang, getLangTableForLanguage(lang)))
                .collect(Collectors.collectingAndThen(Collectors.toMap(Pair::getFirst, Pair::getSecond)
                        , Collections::unmodifiableMap));
    }

    private Map<String, String> getLangTableForLanguage(Language language) {
        ClassPathResource classPathResource
                = new ClassPathResource("/langs/" + language.getKey() + ".properties");

        if (!classPathResource.exists()) {
            throw new IllegalStateException(language.getKey() + ".properties doesn't exist");
        }
        try {
            EncodedResource encodedResource = new EncodedResource(classPathResource, StandardCharsets.UTF_8);
            Properties props = PropertiesLoaderUtils.loadProperties(encodedResource);
            return processLangTable(props);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read property file", e);
        }
    }

    private Map<String, String> processLangTable(Properties props) {
        Map<String, String> map = Maps.fromProperties(props);
        Map<String, String> table = new TreeMap<>();
        Pattern p = Pattern.compile("(%(.)*?%)");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue();
            String key = entry.getKey();
            assert value != null;
            Matcher m = p.matcher(value);
            while (m.find()) {
                String placeholder = m.group().replace("%", "");
                if (map.containsKey(placeholder)) {
                    value = value.replace("%" + placeholder + "%", Objects.requireNonNull(map.get(placeholder)));
                }
            }
            table.put(key, value);
        }
        return table;
    }
}
