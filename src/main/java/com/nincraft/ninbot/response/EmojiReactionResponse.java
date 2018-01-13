package com.nincraft.ninbot.response;

import com.nincraft.ninbot.util.EmojiUtils;
import com.nincraft.ninbot.util.MessageUtils;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.ArrayList;
import java.util.List;

public class EmojiReactionResponse implements IReactionResponse {

    private List<String> emojiList;

    public EmojiReactionResponse(String response) {
        emojiList = new ArrayList<>();
        for (char c : response.toCharArray()) {
            emojiList.add(EmojiUtils.getLetterEmoji(c));
        }
    }

    @Override
    public void addReaction(String reaction) {
        if (emojiList == null) {
            emojiList = new ArrayList<>();
        }
        emojiList.add(reaction);
    }

    @Override
    public void react(Message message, MessageChannel channel) {
        for (String emoji : emojiList) {
            MessageUtils.addReaction(message, emoji);
        }
    }
}
