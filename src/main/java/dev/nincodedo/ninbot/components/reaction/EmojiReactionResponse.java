package dev.nincodedo.ninbot.components.reaction;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmojiReactionResponse extends ReactionResponse {

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

    public EmojiReactionResponse(ReactionResponse reactionResponse) {
        super(reactionResponse);
        addEmojis();
    }

    public EmojiReactionResponse(String response) {
        this.response = response;
        addEmojis();
    }

    private static String getLetterEmoji(char c) {
        return letterMap.get(Character.toString(c).toUpperCase());
    }

    private void addEmojis() {
        emojiList = new ArrayList<>();
        for (char c : response.toCharArray()) {
            emojiList.add(getLetterEmoji(c));
        }
    }

    @Override
    public void react(Message message, MessageChannel channel) {
        emojiList.forEach(emoji -> message.addReaction(Emoji.fromFormatted(emoji)).queue());
    }
}
