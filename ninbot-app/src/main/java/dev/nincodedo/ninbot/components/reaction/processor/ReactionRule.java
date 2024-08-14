package dev.nincodedo.ninbot.components.reaction.processor;

import dev.nincodedo.nincord.ruleprocessing.Rule;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.util.Optional;

public interface ReactionRule extends Rule<ReactionContext> {

    default boolean canProcess(ReactionContext reactionContext) {
        return reactionContext.getReactionMessage().contains(getReplaceTarget());
    }

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
