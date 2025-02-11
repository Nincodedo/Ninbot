package dev.nincodedo.nincord.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.Contract;

import java.util.List;

public class StringSelectMenuInteractionCommandMessageExecutor extends EphemeralMessageExecutor {

    private StringSelectInteractionEvent event;

    public StringSelectMenuInteractionCommandMessageExecutor(StringSelectInteractionEvent event) {
        super();
        this.event = event;
    }

    @Contract("_ -> fail")
    @Override
    protected ModalCallbackAction replyModal(Modal modal) {
        throw new UnsupportedOperationException("Can't modal here");
    }

    @Override
    protected ReplyCallbackAction replyMessage(MessageCreateData message) {
        return event.reply(message);
    }

    @Override
    protected ReplyCallbackAction replyEmbeds(List<MessageEmbed> messageEmbeds) {
        return event.replyEmbeds(messageEmbeds);
    }

    @Override
    public MessageChannel getChannel() {
        return event.getChannel();
    }

    @Override
    public Message getMessage() {
        return event.getMessage();
    }
}
