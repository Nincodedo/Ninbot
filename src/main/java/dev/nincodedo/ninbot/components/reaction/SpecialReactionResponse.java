package dev.nincodedo.ninbot.components.reaction;

import lombok.val;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class SpecialReactionResponse extends ReactionResponse {


    public SpecialReactionResponse(ReactionResponse response) {
        super(response);
    }

    @Override
    public void react(Message message, MessageChannel channel) {
        boolean canReact = true;
        String reactionResponse = response;
        if (reactionResponse.contains("$message.previous")) {
            val lastMessageOptional = getPreviousMessage(channel, message);
            if (reactionResponse.contains("$message.previous.author") && lastMessageOptional.isPresent()) {
                val lastMessage = lastMessageOptional.get();
                val previousAuthorName = lastMessage.getMember() != null ? lastMessage.getMember()
                        .getEffectiveName() : lastMessage.getAuthor().getName();
                reactionResponse = reactionResponse.replace("$message.previous.author", previousAuthorName);
            }
            if (reactionResponse.contains("$message.previous.content") && lastMessageOptional.isPresent()) {
                val lastMessage = lastMessageOptional.get();
                String previousMessageContent = lastMessage.getContentStripped();
                if (reactionResponse.contains("#toUpper")) {
                    reactionResponse = reactionResponse.replace("#toUpper", "");
                    previousMessageContent = previousMessageContent.toUpperCase();
                }
                if (StringUtils.isBlank(previousMessageContent)) {
                    canReact = false;
                }
                reactionResponse = reactionResponse.replace("$message.previous.content", previousMessageContent);
            }
        }
        if (canReact) {
            channel.sendMessage(reactionResponse).queue();
        }
    }

    private Optional<Message> getPreviousMessage(MessageChannel channel, Message message) {
        val messageHistory = channel.getHistoryBefore(message, 1).complete().getRetrievedHistory();
        if (!messageHistory.isEmpty() && !messageHistory.get(0).isWebhookMessage()) {
            return Optional.of(messageHistory.get(0));
        } else {
            return Optional.empty();
        }
    }
}
