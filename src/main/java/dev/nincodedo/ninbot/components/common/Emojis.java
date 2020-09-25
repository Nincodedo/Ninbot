package dev.nincodedo.ninbot.components.common;

import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class Emojis {
    public static final String CHECK_MARK = "\u2705";
    public static final String QUESTION_MARK = "\u2754";
    public static final String CROSS_X = "\u274C";
    public static final String OFF = "\uD83D\uDCF4";
    public static final String ON = "\uD83D\uDD1B";
    public static final String PLUS = "\u2795";
    public static final String SICK_FACE = "\uD83E\uDD22";
    public static final String HAPPY_FACE = "\uD83D\uDE01";
    public static final String PARTY_FACE = "\uD83E\uDD73";
    public static final String BIRTHDAY_CAKE = "\uD83C\uDF82";
    public static final String BALLOON = "\uD83C\uDF88";
    public static final String PARTY_POPPER = "\uD83C\uDF89";
    public static final String PILLS = "\uD83D\uDC8A";
    public static final String COOLDOWN_5_CLOCK = "\uD83D\uDD54";
    @Getter
    private static final Map<Integer, String> numberMap;

    static {
        numberMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            String enclosingKeycap = "️⃣";
            numberMap.put(i, i + enclosingKeycap);
        }
    }
}
