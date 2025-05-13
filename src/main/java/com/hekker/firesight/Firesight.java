package com.hekker.firesight;

import com.hekker.firesight.config.Configs.Generic;
import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Firesight implements ClientModInitializer {
    public static final Logger logger = LoggerFactory.getLogger("firesight");

    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
    }

    public static void debugLog(String msg, Object... args) {
        if (Generic.DEBUG_LOGGING.getBooleanValue()) {
            logger.info(msg, args);
        }

    }
}