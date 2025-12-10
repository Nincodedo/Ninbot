package dev.nincodedo.nincord.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.List;

public class MessageContextInteractionEventMessageExecutor extends EphemeralMessageExecutor {

    private MessageContextInteractionEvent messageContextInteractionEvent;

    public MessageContextInteractionEventMessageExecutor(
            MessageContextInteractionEvent messageContextInteractionEvent) {
        super();
        this.messageContextInteractionEvent = messageContextInteractionEvent;
    }

    @Override
    protected ModalCallbackAction replyModal(Modal modal) {
        return messageContextInteractionEvent.replyModal(modal);
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
    public MessageChannel getChannel() {
        return messageContextInteractionEvent.getChannel();
    }

    @Override
    public Message getMessage() {
        return messageContextInteractionEvent.getTarget();
    }
}
