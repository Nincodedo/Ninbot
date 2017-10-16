package com.nincraft.ninbot.util;

import java.util.HashMap;
import java.util.Map;

public class EmojiUtils {
    private static Map<String, String> letterMap;

    static {
        letterMap = new HashMap<>();
        letterMap.put("A", Emoji.A);
        letterMap.put("E", Emoji.E);
        letterMap.put("M", Emoji.M);
        letterMap.put("S", Emoji.S);
    }

    public static String getLetterEmoji(char c) {
        return letterMap.get(Character.toString(c).toUpperCase());
    }
}
