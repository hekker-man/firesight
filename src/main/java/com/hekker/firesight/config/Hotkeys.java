package com.hekker.firesight.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigHotkey;

import java.util.List;

public class Hotkeys
{
    private static final String HOTKEYS_KEY = "firesight.config.hotkeys";

    public static final ConfigHotkey RENDERING_TOGGLE;
    public static final ConfigHotkey OPEN_CONFIG_GUI;
    public static final List<ConfigHotkey> HOTKEY_LIST;

    static
    {
        RENDERING_TOGGLE = (ConfigHotkey) new ConfigHotkey(
                "renderingToggle",
                "F8",
                "Toggle Firesight rendering",
                "firesight.config.hotkeys.renderingToggle"
        );

        OPEN_CONFIG_GUI = (ConfigHotkey) new ConfigHotkey(
                "openConfigGui",
                "M,F8",
                "Open the Firesight config menu",
                "firesight.config.hotkeys.openConfigGui"
        );

        HOTKEY_LIST = ImmutableList.of(
                RENDERING_TOGGLE,
                OPEN_CONFIG_GUI
        );
    }
}
