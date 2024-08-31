package org.evpro.bookshopV4.servlet.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebListener
public class DatabaseInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Initializing database...");
        try {
            org.evpro.bookshopV4.utilities.DatabaseInitializer.initializeDatabase();
            log.info("Database initialized successfully.");
        } catch (Exception e) {
            log.info("Failed to initialize database: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}