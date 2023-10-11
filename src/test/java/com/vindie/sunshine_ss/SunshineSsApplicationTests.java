package com.vindie.sunshine_ss;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import com.vindie.sunshine_ss.common.email.EmailSenderService;
import com.vindie.sunshine_ss.common.record.event.ss.SsEvent;
import com.vindie.sunshine_ss.common.service.PropService;
import com.vindie.sunshine_ss.push_and_socket.kinda_socket.KindaSocketService;
import com.vindie.sunshine_ss.push_and_socket.push.PushService;
import com.vindie.sunshine_ss.utils.DatabaseCleaner;
import com.vindie.sunshine_ss.utils.EventUtils;
import org.awaitility.core.ConditionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("unit-test")
@SpringBootTest(classes = SunshineSsApplication.class)
@TestPropertySource(locations = "classpath:test.properties")
public abstract class SunshineSsApplicationTests {
    protected static final Faker FAKER = Faker.instance();
    protected static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    @Autowired
    private DatabaseCleaner databaseCleaner;
    @Autowired
    private EventUtils eventUtils;
    @Autowired
    protected PropService propService;
    @MockBean
    private EmailSenderService emailSenderService;
    @MockBean
    private PushService pushService;
    @Autowired
    private KindaSocketService kindaSocketService;

    @AfterEach
    protected void afterEach() throws Exception {
        kindaSocketService.destroy();
    }

    protected ConditionFactory checkEverySec() {
        return checkEverySec(10);
    }

    protected ConditionFactory checkEverySec(int timeoutInSec) {
        return await().pollInterval(1, TimeUnit.SECONDS)
                .timeout(Duration.of(timeoutInSec, ChronoUnit.SECONDS));
    }

    protected void checkInSec(int timeoutInSec, Supplier<Boolean> supplier) {
        try {
            Thread.sleep(timeoutInSec * 1000L);
            assertEquals(Boolean.TRUE, supplier.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean eventEquals(SsEvent.Type type) {
        return eventUtils.event != null && eventUtils.event.getType() == type;
    }

    protected SsEvent getEventAndClean() {
        SsEvent result = eventUtils.event;
        eventUtils.event = null;
        return result;
    }

    @BeforeEach
    public void prepareData() {
        eventUtils.event = null;
        databaseCleaner.clearTables();
    }

}
