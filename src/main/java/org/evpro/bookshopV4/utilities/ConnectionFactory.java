package org.evpro.bookshopV4.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    private static final String PROP_FILE_PATH = "config/database.properties";
    private static String url;
    private static String user;
    private static String password;

    static {
        loadDatabaseProperties();
    }

    private static void loadDatabaseProperties() {
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream(PROP_FILE_PATH)) {
            prop.load(fis);

            url = prop.getProperty("db.url");
            user = prop.getProperty("db.user");
            password = prop.getProperty("db.password");

        } catch (IOException e) {
            throw new RuntimeException("Failed to load database properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}