package dev.nincodedo.ninbot.components.common;

import java.util.HashMap;
import java.util.Map;

public final class Emojis {
    public static final String CHECK_MARK = "✅";
    public static final String QUESTION_MARK = "❔";
    public static final String CROSS_X = "❌";
    public static final String OFF = "\ud83d\udcf4";
    public static final String ON = "\ud83d\udd1b";
    public static final String PLUS = "➕";
    public static final String SICK_FACE = "\ud83e\udd22";
    public static final String HAPPY_FACE = "\ud83d\ude01";
    public static final String PARTY_FACE = "\ud83e\udd73";
    public static final String BIRTHDAY_CAKE = "\ud83c\udf82";
    public static final String BALLOON = "\ud83c\udf88";
    public static final String PARTY_POPPER = "\ud83c\udf89";
    public static final String PILLS = "\ud83d\udc8a";
    public static final String COOLDOWN_5_CLOCK = "\ud83d\udd54";
    public static final String PACMAN_LOADING_ID = "762932479811125248";
    private static final Map<Integer, String> numberMap;

    static {
        numberMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            String enclosingKeycap = "️⃣";
            numberMap.put(i, i + enclosingKeycap);
        }
    }


    private Emojis() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }


    public static Map<Integer, String> getNumberMap() {
        return Emojis.numberMap;
    }
}
