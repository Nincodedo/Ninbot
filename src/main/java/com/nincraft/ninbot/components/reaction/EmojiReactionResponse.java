package com.nincraft.ninbot.components.reaction;

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
    public void react(Message message, MessageChannel channel) {
        emojiList.forEach(emoji -> MessageUtils.addReaction(message, emoji));
    }
}
