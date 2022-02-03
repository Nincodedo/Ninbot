package dev.nincodedo.ninbot.common.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

public class MessageContextInteractionEventMessageExecutor extends MessageExecutor<MessageContextInteractionEventMessageExecutor> {

    private MessageContextInteractionEvent messageContextInteractionEvent;

    public MessageContextInteractionEventMessageExecutor(
            MessageContextInteractionEvent messageContextInteractionEvent) {
        super();
        this.messageContextInteractionEvent = messageContextInteractionEvent;
    }

    @Override
    public void executeMessageActions() {
        if (!messageResponses.isEmpty()) {
            messageResponses.forEach(message -> messageContextInteractionEvent.reply(message).queue());
        }
    }

    @Override
    public MessageContextInteractionEventMessageExecutor returnThis() {
        return this;
    }

    @Override
    public MessageChannel getChannel() {
        return messageContextInteractionEvent.getChannel();
    }

    @Override
    public Message getMessage() {
        return messageContextInteractionEvent.getTarget();
    }
}
