package com.hekker.firesight;

import com.hekker.firesight.config.Configs;
import com.hekker.firesight.event.InputHandler;
import com.hekker.firesight.event.KeyCallbacks;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.config.ConfigManager;
import net.minecraft.client.MinecraftClient;

public class InitHandler implements IInitializationHandler {
    public void registerModHandlers() {
        ConfigManager.getInstance().registerConfigHandler("firesight", new Configs());

        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());
        InputEventHandler.getInputManager().registerKeyboardInputHandler(InputHandler.getInstance());
        InputEventHandler.getInputManager().registerMouseInputHandler(InputHandler.getInstance());
        KeyCallbacks.init(MinecraftClient.getInstance());
    }
}
