package org.evpro.bookshopV4.utilities;

import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

@Slf4j
public class DatabaseInitializer {

    public static void initializeDatabase() {
        log.info("Starting database initialization...");
        try (Connection conn = ConnectionFactory.getConnection()) {
            log.info("Database connection established.");
            executeScript(conn, "schema.sql");
            executeScript(conn, "data.sql");
            log.info("Database initialization completed successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize database", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private static void executeScript(Connection conn, String scriptName) throws Exception {
        log.info("Executing script: {}", scriptName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                DatabaseInitializer.class.getClassLoader().getResourceAsStream(scriptName)));
             Statement stmt = conn.createStatement()) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                    continue; // Skip empty lines and comments
                }
                sb.append(line);
                if (line.trim().endsWith(";")) {
                    String sql = sb.toString().trim();
                    log.debug("Executing SQL: {}", sql);
                    stmt.execute(sql);
                    sb.setLength(0);
                }
            }
        }
        log.info("Script {} executed successfully", scriptName);
    }
}