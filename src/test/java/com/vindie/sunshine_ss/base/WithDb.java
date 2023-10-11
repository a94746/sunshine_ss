package com.vindie.sunshine_ss.base;

import com.vindie.sunshine_ss.SunshineSsApplicationTests;
import org.testcontainers.containers.MySQLContainer;

import java.time.ZoneOffset;
import java.util.Random;
import java.util.TimeZone;

public abstract class WithDb extends SunshineSsApplicationTests {
    private static final int SOCKET_IO_PORT = 33000 + new Random().nextInt(100);
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
        System.setProperty("SOCKET_IO_PORT", String.valueOf(SOCKET_IO_PORT));
    }
}
