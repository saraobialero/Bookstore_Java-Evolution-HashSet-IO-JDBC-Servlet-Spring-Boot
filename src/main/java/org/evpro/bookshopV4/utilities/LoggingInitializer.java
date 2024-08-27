package org.evpro.bookshopV4.utilities;

import org.slf4j.bridge.SLF4JBridgeHandler;

public class LoggingInitializer {
    public static void initialize() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}
