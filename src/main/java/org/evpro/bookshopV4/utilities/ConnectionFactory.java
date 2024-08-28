package org.evpro.bookshopV4.utilities;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@Slf4j
public class ConnectionFactory {
    private static final String PROP_FILE_NAME = "database.properties";
    private static String url;
    private static String user;
    private static String password;
    private static String dbName;

    static {
        try {
            loadDatabaseProperties();
            loadMySQLDriver();
        } catch (Exception e) {
            log.error("Failed to initialize ConnectionFactory", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void loadDatabaseProperties() throws IOException {
        log.info("Loading database properties from {}", PROP_FILE_NAME);
        Properties prop = new Properties();
        try (InputStream inputStream = ConnectionFactory.class.getClassLoader().getResourceAsStream(PROP_FILE_NAME)) {
            if (inputStream == null) {
                throw new IOException("Unable to find " + PROP_FILE_NAME);
            }
            prop.load(inputStream);

            url = prop.getProperty("db.url");
            user = prop.getProperty("db.user");
            password = prop.getProperty("db.password");

            if (url == null || user == null || password == null) {
                throw new IOException("Missing required properties in " + PROP_FILE_NAME);
            }

            log.info("Database properties loaded successfully. URL: {}", url);

            int lastSlashIndex = url.lastIndexOf("/");
            if (lastSlashIndex != -1 && lastSlashIndex < url.length() - 1) {
                dbName = url.substring(lastSlashIndex + 1);
                url = url.substring(0, lastSlashIndex);
            } else {
                throw new IOException("Invalid database URL format in " + PROP_FILE_NAME);
            }

            log.info("Parsed database name: {}", dbName);
        }
    }

    private static void loadMySQLDriver() throws ClassNotFoundException {
        log.info("Loading MySQL JDBC Driver");
        Class.forName("com.mysql.cj.jdbc.Driver");
        log.info("MySQL JDBC Driver registered successfully");
    }

    public static Connection getConnection() throws SQLException {
        createDatabaseIfNotExists();
        log.info("Attempting to establish database connection to {}...", dbName);
        Connection conn = DriverManager.getConnection(url + "/" + dbName, user, password);
        log.info("Database connection established successfully.");
        return conn;
    }

    private static void createDatabaseIfNotExists() throws SQLException {
        log.info("Checking if database {} exists", dbName);
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            log.info("Database {} created or already exists", dbName);
        }
    }
}