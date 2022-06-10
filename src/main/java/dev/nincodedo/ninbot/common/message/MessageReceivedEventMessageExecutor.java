package dev.nincodedo.ninbot.common.message;

import dev.nincodedo.ninbot.common.logging.FormatLogObject;
import dev.nincodedo.ninbot.common.message.impersonation.Impersonation;
import dev.nincodedo.ninbot.common.message.impersonation.Impersonator;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.ExecutionException;

@Slf4j
public class MessageReceivedEventMessageExecutor extends MessageExecutor<MessageReceivedEventMessageExecutor> {

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

    private void sendMessage(Message message) {
        if (impersonation == null || getChannel().getType().isThread()) {
            getChannel().sendMessage(message).queue();
        } else {
            Impersonator impersonator = new Impersonator(impersonation, getGuild(),
                    messageReceivedEvent.getChannel());
            try {
                impersonator.sendMessage(message).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Failed to send webhook message in server {}", FormatLogObject.guildName(getGuild()), e);
            }
        }
    }

    @Override
    public MessageReceivedEventMessageExecutor returnThis() {
        return this;
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
