package com.vindie.sunshine_ss;

import com.github.javafaker.Faker;
import com.vindie.sunshine_ss.utils.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;

import java.time.ZoneOffset;
import java.util.TimeZone;

@ActiveProfiles("unit-test")
@SpringBootTest(classes = SunshineSsApplication.class)
@TestPropertySource(locations = "classpath:test.properties")
public abstract class SunshineSsApplicationTests {
    protected static final Faker FAKER = Faker.instance();
    private static final int MYSQL_PORT = 3306;

    @Autowired
    private DatabaseCleaner databaseCleaner;
    public static final MySQLContainer<?> MY_SQL = new MySQLContainer<>("mysql:8.0.28")
            .withExposedPorts(MYSQL_PORT)
            .withDatabaseName("sunshine")
            .withUsername("root")
            .withPassword("root");

    static {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
        MY_SQL.start();
        System.setProperty("MYSQL_HOST", MY_SQL.getContainerIpAddress());
        System.setProperty("MYSQL_PORT", MY_SQL.getMappedPort(MYSQL_PORT).toString());
    }

    @BeforeEach
    public void prepareData() {
        databaseCleaner.clearTables();
    }

}
