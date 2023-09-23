package com.vindie.sunshine_ss.interfaces;

import com.vindie.sunshine_ss.SunshineSsApplicationTests;
import org.testcontainers.containers.MySQLContainer;

import java.time.ZoneOffset;
import java.util.TimeZone;

public abstract class WithSqlContener extends SunshineSsApplicationTests {
    private static final int MYSQL_PORT = 3306;
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
}
