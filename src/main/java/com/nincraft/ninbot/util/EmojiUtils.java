package com.nincraft.ninbot.util;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class EmojiUtils {
    private static Map<String, String> letterMap;

    static {
        letterMap = new HashMap<>();
        letterMap.put("A", Emoji.A);
        letterMap.put("E", Emoji.E);
        letterMap.put("H", Emoji.H);
        letterMap.put("K", Emoji.K);
        letterMap.put("M", Emoji.M);
        letterMap.put("N", Emoji.N);
        letterMap.put("S", Emoji.S);
        letterMap.put("T", Emoji.T);
    }

    public static String getLetterEmoji(char c) {
        return letterMap.get(Character.toString(c).toUpperCase());
    }
}
