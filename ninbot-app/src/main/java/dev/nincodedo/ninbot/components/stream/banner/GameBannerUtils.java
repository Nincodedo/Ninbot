package dev.nincodedo.ninbot.components.stream.banner;

import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class GameBannerUtils {

    public static int getLogoIdFromFile(File bannerFile) throws NumberFormatException {
        return Integer.parseInt(bannerFile.getName().split("-")[2]);
    }

    public static int getBackgroundIdFromFile(File bannerFile) throws NumberFormatException {
        return Integer.parseInt(bannerFile.getName().split("-")[3].split("\\.")[0]);
    }

    public static String getCachedFileName(String gameTitle) {
        if (gameTitle == null) {
            return null;
        } else {
            return gameTitle.replaceAll("[^A-Za-z0-9]", "");
        }
    }
}
