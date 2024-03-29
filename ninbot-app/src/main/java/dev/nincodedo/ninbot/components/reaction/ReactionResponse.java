package dev.nincodedo.ninbot.components.reaction;

import lombok.Data;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.regex.Pattern;

@Data
public class ReactionResponse {

    protected String response;
    protected String target;
    protected List<String> responses;
    protected ReactionMatchType reactionMatchType;

    public ReactionResponse() {
    }

    public ReactionResponse(ReactionResponse reactionResponse) {
        this.response = reactionResponse.response;
        this.target = reactionResponse.target;
        this.responses = reactionResponse.responses;
        this.reactionMatchType = reactionResponse.reactionMatchType;
    }

    void react(Message message, MessageChannel channel) {
        //NO-OP
    }

    boolean canRespond(MessageReceivedEvent event) {
        return switch (reactionMatchType) {
            case EXACT -> getTarget().equalsIgnoreCase(event.getMessage().getContentStripped());
            case CONTAINS -> event.getMessage().getContentStripped().toLowerCase().contains(getTarget().toLowerCase());
            case REGEX -> Pattern.matches(getTarget(), event.getMessage().getContentStripped().replace("\n", ""));
        };
    }
}
