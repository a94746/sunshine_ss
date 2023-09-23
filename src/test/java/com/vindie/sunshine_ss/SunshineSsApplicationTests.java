package com.vindie.sunshine_ss;

import com.github.javafaker.Faker;
import com.vindie.sunshine_ss.common.ss_event.SsEvent;
import com.vindie.sunshine_ss.utils.DatabaseCleaner;
import com.vindie.sunshine_ss.utils.EventUtils;
import org.awaitility.core.ConditionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@ActiveProfiles("unit-test")
@SpringBootTest(classes = SunshineSsApplication.class)
@TestPropertySource(locations = "classpath:test.properties")
public abstract class SunshineSsApplicationTests {
    protected static final Faker FAKER = Faker.instance();
    @Autowired
    private DatabaseCleaner databaseCleaner;
    @Autowired
    private EventUtils eventUtils;

    protected ConditionFactory checkEverySec() {
        return checkEverySec(10);
    }

    protected ConditionFactory checkEverySec(int timeoutInSec) {
        return await().pollInterval(1, TimeUnit.SECONDS)
                .timeout(Duration.of(timeoutInSec, ChronoUnit.SECONDS));
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
