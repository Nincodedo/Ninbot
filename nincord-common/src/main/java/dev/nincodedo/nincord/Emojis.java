package dev.nincodedo.nincord;

import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@UtilityClass
public class Emojis {
    public static final String CHECK_MARK = "✅";
    public static final String CROSS_X = "❌";
    public static final String OFF = "\uD83D\uDCF4";
    public static final String ON = "\uD83D\uDD1B";
    public static final String PLUS = "➕";
    public static final String SICK_FACE = "\uD83E\uDD22";
    public static final String HAPPY_FACE = "\uD83D\uDE01";
    public static final String PARTY_FACE = "\uD83E\uDD73";
    public static final String BIRTHDAY_CAKE = "\uD83C\uDF82";
    public static final String BALLOON = "\uD83C\uDF88";
    public static final String PARTY_POPPER = "\uD83C\uDF89";
    public static final String PILLS = "\uD83D\uDC8A";
    public static final String THUMBS_UP = "\uD83D\uDC4D";
    private static final List<String> DOCTOR_LIST = Arrays.asList("\uD83D\uDC68\u200D⚕️", "\uD83D\uDC69\u200D⚕️");
    @Getter
    private static final Map<Integer, String> numberMap;
    private static Random random = new SecureRandom();

    static {
        numberMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            String enclosingKeycap = "️⃣";
            numberMap.put(i, i + enclosingKeycap);
        }
    }

    public static String getRandomDoctorEmoji() {
        return DOCTOR_LIST.get(random.nextInt(DOCTOR_LIST.size()));
    }

    /**
     * Returns the appropriate check or x emoji based on the passed in boolean of true or false.
     *
     * @param decidingBoolean boolean deciding factor
     * @return returns check for true and x for false
     */
    public static String getCheckOrXResponse(boolean decidingBoolean) {
        return decidingBoolean ? CHECK_MARK : CROSS_X;
    }
}
