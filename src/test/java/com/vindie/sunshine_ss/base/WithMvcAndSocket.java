package com.vindie.sunshine_ss.base;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vindie.sunshine_ss.common.record.event.ui.UiEvent;
import com.vindie.sunshine_ss.security.record.JwtAuthenticationResponse;
import com.vindie.sunshine_ss.security.record.SigninRequest;
import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.engineio.client.Transport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.vindie.sunshine_ss.security.config.RequestFilter.MY_HEADER_NAME;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class WithMvcAndSocket extends WithData {
    @Value("${flow.my-header-code}")
    protected String myHeaderCode;
    @Value("${socketio.host}")
    private String socketIOHost;
    @Value("${socketio.port}")
    private int socketIOPort;
    protected Socket socket;
    @Autowired
    protected MockMvc mvc;
    protected String token;

    @BeforeEach
    @Transactional
    protected void beforeEach() throws Exception {
        authAccount();
        socketCreate();
    }

    @AfterEach
    protected void afterEach() throws Exception {
        socket.close();
    }

    private void socketCreate() throws URISyntaxException {
        socket = IO.socket("http://" + socketIOHost + ":" + socketIOPort);
        socket.io().on(Socket.EVENT_CONNECT_ERROR, args -> {throw new RuntimeException((String) args[0]);});
        socket.io().on(Manager.EVENT_TRANSPORT, args -> {
            Transport transport = (Transport) args[0];
            transport.on(Transport.EVENT_REQUEST_HEADERS, args2 -> {
                @SuppressWarnings("unchecked")
                Map<String, List<String>> headers = (Map<String, List<String>>) args2[0];
                headers.put(HttpHeaders.AUTHORIZATION, List.of("Bearer " + token));
            });
        });
    }

    protected void authAccount() throws Exception {
        var request = SigninRequest.builder()
                .email(account.getCread().getEmail())
                .pass(PASS)
                .uniqueId(account.getDevices().get(0).getUniqueId())
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

    protected void checkSocket(UiEvent uiEvent) throws Exception {
        checkSocket(uiEvent, 10);
    }

    protected void checkSocket(UiEvent uiEvent, int timeoutInSec) throws Exception {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        socket.on(uiEvent.name(), args -> future.complete(true));
        assertTrue(future.get(timeoutInSec, TimeUnit.SECONDS));
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
