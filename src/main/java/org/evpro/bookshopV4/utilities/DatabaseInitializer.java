package org.evpro.bookshopV4.utilities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        try (Connection conn = ConnectionFactory.getConnection()) {
            executeScript(conn, "schema.sql");
            executeScript(conn, "data.sql");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private static void executeScript(Connection conn, String scriptName) throws Exception {
        String script = readResourceFile(scriptName);
        try (Statement stmt = conn.createStatement()) {
            for (String sql : script.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql);
                }
            }
        }
    }

    private static String readResourceFile(String fileName) throws Exception {
        try (InputStream is = DatabaseInitializer.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) throw new Exception("Resource not found: " + fileName);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }
}