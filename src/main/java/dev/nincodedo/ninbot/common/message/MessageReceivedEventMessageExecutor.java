package dev.nincodedo.ninbot.common.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageReceivedEventMessageExecutor extends MessageExecutor<MessageReceivedEventMessageExecutor> {

    private MessageReceivedEvent messageReceivedEvent;

    public MessageReceivedEventMessageExecutor(MessageReceivedEvent event) {
        this.messageReceivedEvent = event;
    }

    @Override
    public void executeMessageActions() {
        if (!messageResponses.isEmpty()) {
            messageResponses.forEach(message -> getChannel().sendMessage(message).queue());
        }
    }

    @Override
    public MessageReceivedEventMessageExecutor returnThis() {
        return this;
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
