package com.nincraft.ninbot.util;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class EmojiUtils {
    private static Map<String, String> letterMap;

    static {
        letterMap = new HashMap<>();
        char unicodeChar = '\uDDE6';
        char letterChar = 'A';
        for (int i = 0; i < 26; i++) {
            letterMap.put(String.valueOf(letterChar), "\uD83C" + unicodeChar);
            letterChar++;
            unicodeChar++;
        }
    }

    public static String getLetterEmoji(char c) {
        return letterMap.get(Character.toString(c).toUpperCase());
    }
}
