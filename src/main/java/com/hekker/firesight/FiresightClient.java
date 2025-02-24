package com.hekker.firesight;

import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiresightClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Firesight.LOGGER.info("Loading Firesight client");
    }
}