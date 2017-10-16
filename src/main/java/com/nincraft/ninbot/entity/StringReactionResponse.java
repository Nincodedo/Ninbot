package com.nincraft.ninbot.entity;

import com.nincraft.ninbot.util.MessageUtils;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class StringReactionResponse implements IReactionResponse {

    private String reaction;

    public StringReactionResponse(String response) {
        this.reaction = response;
    }

    @Override
    public void addReaction(String reaction) {
        this.reaction = reaction;
    }

    @Override
    public void react(Message message, MessageChannel channel) {
        MessageUtils.sendMessage(channel, reaction);
    }
}
