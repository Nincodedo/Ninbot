package dev.nincodedo.ninbot.components.common;

import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.*;

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
    public static final String PACMAN_LOADING_ID = "762932479811125248";
    public static final List<String> DOCTOR_LIST = Arrays.asList("\uD83D\uDC68\u200D⚕️", "\uD83D\uDC69\u200D⚕️");
    public static final String THUMBS_UP = "\uD83D\uDC4D";
    @Getter
    private static final Map<Integer, String> numberMap;

    static {
        numberMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            String enclosingKeycap = "️⃣";
            numberMap.put(i, i + enclosingKeycap);
        }
    }

    public static String getRandomDoctorEmoji() {
        return DOCTOR_LIST.get(new Random().nextInt(DOCTOR_LIST.size()));
    }
}
