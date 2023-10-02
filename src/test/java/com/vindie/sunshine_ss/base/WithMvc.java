package com.vindie.sunshine_ss.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vindie.sunshine_ss.security.record.JwtAuthenticationResponse;
import com.vindie.sunshine_ss.security.record.SigninRequest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

import static com.vindie.sunshine_ss.security.config.RequestFilter.MY_HEADER_NAME;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class WithMvc extends WithDbData {
    @Value("${app.my-header-code}")
    protected String myHeaderCode;
    @Autowired
    protected MockMvc mvc;
    protected String token;

    @BeforeEach
    @Transactional
    protected void authAccount() throws Exception {
        var request = SigninRequest.builder()
                .email(account.getCread().getEmail())
                .pass(PASS)
                .build();
        var response = mvc.perform(post("/auth/signin")
                        .header(MY_HEADER_NAME, myHeaderCode)
                        .content(MAPPER.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        token = MAPPER.readValue(response, JwtAuthenticationResponse.class).getToken();
    }

    protected <T> ResultMatcher modelMatches(TypeReference<T> typeReference, Consumer<T> matcher) {
        return result -> {
            String content = result.getResponse().getContentAsString(UTF_8);
            T model = MAPPER.readValue(content, typeReference);
            matcher.accept(model);
        };
    }

    protected <T> ResultMatcher modelMatches(Class<T> clazz, Consumer<T> matcher) {
        return result -> {
            String content = result.getResponse().getContentAsString(UTF_8);
            T model = MAPPER.readValue(content, clazz);
            matcher.accept(model);
        };
    }

    protected String getJwtHeader() {
        return "Bearer " + token;
    }

}
