package dev.nincodedo.ninbot.common.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.List;

public class MessageContextInteractionEventMessageExecutor extends EphemeralMessageExecutor<MessageContextInteractionEventMessageExecutor> {

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
    protected ReplyCallbackAction replyMessage(MessageCreateData message) {
        return messageContextInteractionEvent.reply(message);
    }

    @Override
    protected ReplyCallbackAction replyEmbeds(List<MessageEmbed> messageEmbeds) {
        return messageContextInteractionEvent.replyEmbeds(messageEmbeds);
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
