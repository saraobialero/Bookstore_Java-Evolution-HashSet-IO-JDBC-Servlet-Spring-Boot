package org.evpro.bookshopV4.servlet.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class LoggingInitializerListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInitializerListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing logging for the application");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Application shutting down");
    }
}