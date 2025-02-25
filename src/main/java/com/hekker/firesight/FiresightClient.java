package com.hekker.firesight;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiresightClient implements ClientModInitializer {
    public static final String MOD_ID = "firesight";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Loading Firesight");
        BlockPos pos = new BlockPos(0, 70, 0);
        LOGGER.info(pos.toString());
    }
}