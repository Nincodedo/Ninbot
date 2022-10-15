package dev.nincodedo.ninbot.components.reaction;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ReactionUtils {
    private static List<String> badCharacters = Arrays.asList(" ", "!", ":");
    @Getter
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

    public static boolean isCanEmoji(String response) {
        if (containsBadCharacters(response)) {
            return false;
        }
        for (char c : response.toCharArray()) {
            String check = StringUtils.replaceOnce(response, Character.toString(c), "");
            if (check.contains(Character.toString(c))) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsBadCharacters(String response) {
        return badCharacters.stream().anyMatch(response::contains);
    }

    public static boolean hasSpecialActionsActions(String response) {
        return response.contains("$");
    }

}
