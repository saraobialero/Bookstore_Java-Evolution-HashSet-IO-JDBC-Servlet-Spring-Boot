package org.evpro.bookshopV4.utilities;

import lombok.extern.slf4j.Slf4j;
import org.evpro.bookshopV4.exception.DatabaseException;
import org.evpro.bookshopV4.exception.ErrorResponse;
import org.evpro.bookshopV4.model.enums.HttpStatusCode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class DatabaseInitializer {

    public static void initializeDatabase() {
        log.info("Starting database initialization...");
        try (Connection connection = ConnectionFactory.getConnection()) {
            log.info("Database connection established.");
            executeScript(connection, "schema.sql");
            if (isDatabaseEmpty(connection)) {
                executeScript(connection, "data.sql");
                log.info("Initial data inserted successfully.");
            } else {
                log.info("Database already contains data. Skipping initial data insertion.");
            }
            log.info("Database initialization completed successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize database", e);
            throw new DatabaseException(
                    "Database initialization error",
                    "Failed to initialize database: " + e.getMessage(),
                    HttpStatusCode.INTERNAL_SERVER_ERROR
            );
        }
    }

    private static boolean isDatabaseEmpty(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM books")) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("42S02")) {
                return true;
            }
            throw e;
        }
        return true;
    }


    private static void executeScript(Connection connection, String scriptName) throws Exception {
        log.info("Executing script: {}", scriptName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            DatabaseInitializer.class.getClassLoader().getResourceAsStream(scriptName)));
             Statement statement = connection.createStatement()) {

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                    continue;
                }
                stringBuilder.append(line);
                if (line.trim().endsWith(";")) {
                    String sql = stringBuilder.toString().trim();
                    log.debug("Executing SQL: {}", sql);
                    statement.execute(sql);
                    stringBuilder.setLength(0);
                }
            }
        }
        log.info("Script {} executed successfully", scriptName);
    }
}