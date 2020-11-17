package de.bsautermeister.bomb.desktop;

import java.nio.file.Paths;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class AssetPacker {
    private static final String RAW_ASSETS_PATH = "desktop/assets-raw";
    private static final String ASSETS_PATH = "android/assets";

    public static void main(String[] args) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxHeight = 1024;
        settings.paddingX = 4;
        settings.paddingY = 4;
        settings.debug = false;

        pack(settings, "game");
        pack(settings, "loading");
        pack(settings, "ui");
    }

    private static void pack(TexturePacker.Settings settings, String name) {
        TexturePacker.process(settings,
                Paths.get(RAW_ASSETS_PATH, name).toString(),
                Paths.get(ASSETS_PATH, name).toString(),
                name);
    }
}
