package dev.nincodedo.ninbot.components.reaction;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

class StringReactionResponse extends ReactionResponse {

    public StringReactionResponse(ReactionResponse reactionResponse) {
        super(reactionResponse);
    }

    @Override
    public void react(Message message, MessageChannel channel) {
        channel.sendMessage(response).queue();
    }
}
