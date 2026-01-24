package com.market.cart.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@RequiredArgsConstructor
public class DatabaseInit {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @PostConstruct
    public void init() {
        // Check if a certain table is empty
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM roles",
                Integer.class
        );

        if (count < 2) {
            runSqlScript();
        }
    }

    private void runSqlScript() {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("sql/all_in_one_to_begin.sql")
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}
