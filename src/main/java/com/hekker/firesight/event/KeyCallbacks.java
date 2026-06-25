package com.hekker.firesight.event;

import com.hekker.firesight.config.Hotkeys;
import com.hekker.firesight.config.Configs.Generic;
import com.hekker.firesight.gui.GuiConfigsFiresight;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBooleanConfigWithMessage;
import net.minecraft.client.Minecraft;

public class KeyCallbacks
{
    public static void init(Minecraft mc)
    {
        Hotkeys.RENDERING_TOGGLE.getKeybind()
                .setCallback(new KeyCallbackToggleBooleanConfigWithMessage(Generic.FIRESIGHT_RENDERING));

        Hotkeys.OPEN_CONFIG_GUI.getKeybind()
                .setCallback((action, key) -> {
                    mc.setScreenAndShow(new GuiConfigsFiresight());
                    return true;
                });
    }
}
