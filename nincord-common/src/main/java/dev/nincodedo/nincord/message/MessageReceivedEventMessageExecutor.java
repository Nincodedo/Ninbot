package dev.nincodedo.nincord.message;

import dev.nincodedo.nincord.message.impersonation.Impersonation;
import dev.nincodedo.nincord.message.impersonation.Impersonator;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Slf4j
public class MessageReceivedEventMessageExecutor extends MessageExecutor {

    private MessageReceivedEvent messageReceivedEvent;
    private Impersonation impersonation = null;

    public MessageReceivedEventMessageExecutor(MessageReceivedEvent event) {
        this.messageReceivedEvent = event;
    }

    @Override
    public void executeMessageActions() {
        if (!messageResponses.isEmpty()) {
            messageResponses.forEach(this::sendMessage);
        }
    }

    public void impersonate(Impersonation impersonation) {
        this.impersonation = impersonation;
    }

    private void sendMessage(MessageCreateData message) {
        if (impersonation == null || getChannel().getType().isThread()) {
            getChannel().sendMessage(message).queue();
        } else {
            Impersonator impersonator = new Impersonator(impersonation, getGuild(),
                    messageReceivedEvent.getChannel());
            impersonator.sendMessage(message);
        }
    }

    public Guild getGuild() {
        return messageReceivedEvent.getGuild();
    }

    @Override
    public MessageChannel getChannel() {
        return messageReceivedEvent.getChannel();
    }

    @Override
    public Message getMessage() {
        return messageReceivedEvent.getMessage();
    }
}
