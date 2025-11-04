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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Configs implements IConfigHandler
{
    private static final String CONFIG_FILE_NAME = "firesight.json";

    public static void loadFromFile()
    {
        Path configFile = FileUtils.getConfigDirectory().toPath().resolve(CONFIG_FILE_NAME);

        if (Files.isRegularFile(configFile) && Files.isReadable(configFile))
        {
            JsonElement el = JsonUtils.parseJsonFile(configFile.toFile());

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
        Path dir = FileUtils.getConfigDirectory().toPath();
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Path configFile = dir.resolve(CONFIG_FILE_NAME);

        JsonObject root = new JsonObject();
        ConfigUtils.writeConfigBase(root, "Generic", Generic.OPTIONS);
        ConfigUtils.writeConfigBase(root, "Hotkeys", Hotkeys.HOTKEY_LIST);

        // If you have a Path version, prefer it:
        // JsonUtils.writeJsonToFileAsPath(root, configFile);
        JsonUtils.writeJsonToFile(root, configFile.toFile());
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
            FIRESIGHT_RENDERING = new ConfigBoolean(
                    "firesightRendering",
                    false,
                    "Toggle the Firesight overlay",
                    "firesight.config.generic.name.firesightRendering"
            );

            DEBUG_LOGGING = new ConfigBoolean(
                    "debugLogging",
                    false,
                    "Write extra debug info to the log",
                    "firesight.config.generic.name.debugLogging"
            );

            SCAN_RANGE = new ConfigInteger(
                    "scanRange",
                    10,
                    1,
                    100,
                    "firesight.config.generic.comment.scanRange",
                    "firesight.config.generic.name.scanRange"
            );

            VISUAL_COLOR = new ConfigColor(
                    "visualColor",
                    "#80FF8000",
                    "firesight.config.generic.comment.visualColor",
                    "firesight.config.generic.name.visualColor"
            );

            OPTIONS = ImmutableList.of(
                    FIRESIGHT_RENDERING,
                    DEBUG_LOGGING,
                    SCAN_RANGE,
                    VISUAL_COLOR
            );
        }
    }
}
