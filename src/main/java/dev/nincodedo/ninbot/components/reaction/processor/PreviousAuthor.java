package dev.nincodedo.ninbot.components.reaction.processor;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Optional;

public class PreviousAuthor implements ReactionProcessor {

    @Override
    public String getReplaceTarget() {
        return "$message.previous.author";
    }

    @Override
    public void process(ReactionContext reactionContext) {
        String reactionMessage = reactionContext.getReactionMessage();
        if (reactionMessage.contains(getReplaceTarget())) {
            var lastMessageOptional = getPreviousMessage(reactionContext.getChannel(), reactionContext.getMessage());
            if (reactionMessage.contains(getReplaceTarget()) && lastMessageOptional.isPresent()) {
                var lastMessage = lastMessageOptional.get();
                var previousAuthorName = lastMessage.getMember() != null ? lastMessage.getMember()
                        .getEffectiveName() : lastMessage.getAuthor().getName();
                reactionMessage = reactionMessage.replace(getReplaceTarget(), previousAuthorName);
            }
        }
        reactionContext.setReactionMessage(reactionMessage);
    }

    private Optional<Message> getPreviousMessage(MessageChannel channel, Message message) {
        if (message.getReferencedMessage() != null) {
            return Optional.of(message.getReferencedMessage());
        }
        var messageHistory = channel.getHistoryBefore(message, 1).complete().getRetrievedHistory();
        if (!messageHistory.isEmpty() && !messageHistory.get(0).isWebhookMessage()) {
            return Optional.of(messageHistory.get(0));
        } else {
            return Optional.empty();
        }
    }
}
