package dev.nincodedo.ninbot.components.reaction.processor;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Optional;

public interface ReactionRule {

    default void process(ReactionContext reactionContext) {
        if (canProcess(reactionContext)) {
            execute(reactionContext);
        }
    }

    default boolean canProcess(ReactionContext reactionContext) {
        return reactionContext.getReactionMessage().contains(getReplaceTarget());
    }

    void execute(ReactionContext reactionContext);

    String getReplaceTarget();

    default Optional<Message> getPreviousMessage(MessageChannel channel, Message message) {
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
