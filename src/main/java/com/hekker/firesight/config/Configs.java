package com.hekker.firesight.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigColor;      // ← NEW
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

import java.io.File;

public class Configs implements IConfigHandler
{
    private static final String CONFIG_FILE_NAME = "firesight.json";

    public static void loadFromFile()
    {
        File file = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);
        if (file.exists() && file.isFile() && file.canRead())
        {
            JsonElement el = JsonUtils.parseJsonFile(file);
            if (el != null && el.isJsonObject())
            {
                JsonObject root = el.getAsJsonObject();
                ConfigUtils.readConfigBase(root, "Generic", Generic.OPTIONS);
                ConfigUtils.readConfigBase(root, "Hotkeys", Hotkeys.HOTKEY_LIST);
            }
        }
    }

    public static void saveToFile()
    {
        File dir = FileUtils.getConfigDirectory();
        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs())
        {
            JsonObject root = new JsonObject();
            ConfigUtils.writeConfigBase(root, "Generic", Generic.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Hotkeys", Hotkeys.HOTKEY_LIST);
            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override public void load() { loadFromFile(); }
    @Override public void save() { saveToFile(); }

    /* -------------------- generic options -------------------- */

    public static class Generic
    {
        public static final ConfigBoolean FIRESIGHT_RENDERING;
        public static final ConfigBoolean DEBUG_LOGGING;
        public static final ConfigInteger SCAN_RANGE;
        public static final ConfigColor   VISUAL_COLOR;
        public static final ImmutableList<IConfigBase> OPTIONS;

        static
        {
            FIRESIGHT_RENDERING = (ConfigBoolean) new ConfigBoolean("firesightRendering", false)
                    .apply("firesight.config.generic");

            DEBUG_LOGGING = (ConfigBoolean) new ConfigBoolean("debugLogging", false)
                    .apply("firesight.config.generic");

            SCAN_RANGE = (ConfigInteger) new ConfigInteger("scanRange", 10, 1, 100)
                    .apply("firesight.config.generic");

            // default = 50 % alpha, orange (#80FF8000 = AARRGGBB)
            VISUAL_COLOR = (ConfigColor) new ConfigColor("visualColor", "#80FF8000")
                    .apply("firesight.config.generic");

            OPTIONS = ImmutableList.of(
                    FIRESIGHT_RENDERING,
                    DEBUG_LOGGING,
                    SCAN_RANGE,
                    VISUAL_COLOR
            );
        }
    }
}
