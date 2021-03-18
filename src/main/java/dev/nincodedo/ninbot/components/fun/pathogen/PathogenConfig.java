package dev.nincodedo.ninbot.components.fun.pathogen;

public final class PathogenConfig {
    private static final String ROLE_NAME = "infected";


    private PathogenConfig() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }


    public static String getROLE_NAME() {
        return PathogenConfig.ROLE_NAME;
    }
}
