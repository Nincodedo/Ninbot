package dev.nincodedo.ninbot.components.reaction;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

@NoArgsConstructor
@Data
abstract class ReactionResponse {

    protected String response;
    protected String target;
    protected List<String> responses;
    protected ReactionMatchType reactionMatchType;

    public ReactionResponse(ReactionResponse reactionResponse) {
        this.response = reactionResponse.response;
        this.target = reactionResponse.target;
        this.responses = reactionResponse.responses;
        this.reactionMatchType = reactionResponse.reactionMatchType;
    }

    abstract void react(Message message, MessageChannel channel);

    boolean canRespond(MessageReceivedEvent event) {
        return switch (reactionMatchType) {
            case EXACT -> getTarget()
                    .equalsIgnoreCase(event.getMessage().getContentStripped());
            case CONTAINS -> event.getMessage()
                    .getContentStripped()
                    .toLowerCase()
                    .contains(getTarget()
                            .toLowerCase());
            case REGEX -> event.getMessage().getContentStripped().matches(getTarget());
        };
    }
}
