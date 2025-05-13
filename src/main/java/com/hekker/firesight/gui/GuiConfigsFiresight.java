package com.hekker.firesight.gui;

import com.google.common.collect.ImmutableList;
import com.hekker.firesight.config.Configs;
import com.hekker.firesight.config.Hotkeys;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase.ConfigOptionWrapper;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;

public class GuiConfigsFiresight extends GuiConfigsBase
{
    public GuiConfigsFiresight()
    {
        super(10, 50, "firesight", (Screen) null,
                "firesight.gui.title.configs");
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        return ConfigOptionWrapper.createFor(
                ImmutableList.<IConfigBase>builder()
                        .addAll(Configs.Generic.OPTIONS)
                        .addAll(Hotkeys.HOTKEY_LIST)
                        .build());
    }
}
