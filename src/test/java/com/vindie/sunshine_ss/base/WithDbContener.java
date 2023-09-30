package com.vindie.sunshine_ss.base;

import com.vindie.sunshine_ss.SunshineSsApplicationTests;
import org.testcontainers.containers.MySQLContainer;

import java.time.ZoneOffset;
import java.util.TimeZone;

public abstract class WithDbContener extends SunshineSsApplicationTests {
    private static final int DB_PORT = 3306;
    public static final MySQLContainer<?> DB = new MySQLContainer<>("mysql:8.0.28")
            .withExposedPorts(DB_PORT)
            .withDatabaseName("sunshine")
            .withUsername("root")
            .withPassword("root");

    static {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
        DB.start();
        System.setProperty("MYSQL_HOST", DB.getHost());
        System.setProperty("MYSQL_PORT", DB.getMappedPort(DB_PORT).toString());
    }
}
