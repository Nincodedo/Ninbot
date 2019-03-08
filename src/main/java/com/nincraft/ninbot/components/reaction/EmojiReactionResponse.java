package com.nincraft.ninbot.components.reaction;

import lombok.Getter;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmojiReactionResponse implements IReactionResponse {

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

    @Getter
    private List<String> emojiList;

    public EmojiReactionResponse(String response) {
        emojiList = new ArrayList<>();
        for (char c : response.toCharArray()) {
            emojiList.add(getLetterEmoji(c));
        }
    }

    private static String getLetterEmoji(char c) {
        return letterMap.get(Character.toString(c).toUpperCase());
    }

    @Override
    public void react(Message message, MessageChannel channel) {
        emojiList.forEach(emoji -> message.addReaction(emoji).queue());
    }
}
