package com.vindie.sunshine_ss.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class DatabaseCleaner {
    private static final Set<String> RESTRICTED_TABLES = Stream.of("DATABASECHANGELOG").collect(Collectors.toSet());

    private DataSource dataSource;

    public void clearTables() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS=OFF");
            ResultSet tables = connection.getMetaData().getTables("sunshine", "sunshine", null, null);
            while (tables.next()){
                String tableName = tables.getString(3);
                if (!RESTRICTED_TABLES.contains(tableName)){
                    statement.execute("DELETE FROM "+tableName);
                }
            }
            statement.execute("SET FOREIGN_KEY_CHECKS=ON");
        } catch (SQLException e) {
            log.error("SQL not executed", e);
            throw new RuntimeException(e);
        }
    }
}
