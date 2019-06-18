package com.nincraft.ninbot.components.reaction;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

public class StringReactionResponse extends ReactionResponse {

    StringReactionResponse(ReactionResponse reactionResponse) {
        this.response = reactionResponse.getResponse();
        this.target = reactionResponse.getTarget();
        this.type = reactionResponse.getType();
    }

    @Override
    public void react(Message message, MessageChannel channel) {
        channel.sendMessage(response).queue();
    }
}
