package com.hekker.firesight;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiresightClient implements ClientModInitializer {
    public static final String MOD_ID = "firesight";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Loading Firesight");

        // Check for Litematica
        try {
            Class.forName("fi.dy.masa.litematica.Litematica");
            LOGGER.info("Litematica detected.");
        } catch (ClassNotFoundException e) {
            LOGGER.error("Litematica not found!");
        }

        // Check for Malilib
        try {
            Class.forName("fi.dy.masa.malilib.MaLiLib");
            LOGGER.info("MaLiLib detected.");
        } catch (ClassNotFoundException e) {
            LOGGER.error("MaLiLib not found!");
        }
    }
}
