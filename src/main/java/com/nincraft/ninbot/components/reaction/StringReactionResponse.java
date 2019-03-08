package com.nincraft.ninbot.components.reaction;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class StringReactionResponse implements IReactionResponse {

    private String reaction;

    StringReactionResponse(String response) {
        this.reaction = response;
    }

    @Override
    public void react(Message message, MessageChannel channel) {
        channel.sendMessage(reaction).queue();
    }
}
