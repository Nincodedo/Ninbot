package dev.nincodedo.ninbot.components.reaction.processor;

import dev.nincodedo.ninbot.components.reaction.ReactionResponse;
import lombok.Data;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

@Data
public class ReactionContext {
    private ReactionResponse reactionResponse;
    private String reactionMessage;
    private Message message;
    private MessageChannel channel;
    private boolean canReact = true;
}
