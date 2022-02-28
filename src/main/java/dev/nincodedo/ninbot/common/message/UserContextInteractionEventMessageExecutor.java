package dev.nincodedo.ninbot.common.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class UserContextInteractionEventMessageExecutor extends MessageExecutor<UserContextInteractionEventMessageExecutor> {

    private UserContextInteractionEvent userContextInteractionEvent;

    public UserContextInteractionEventMessageExecutor(
            @NotNull UserContextInteractionEvent userContextInteractionEvent) {
        this.userContextInteractionEvent = userContextInteractionEvent;
    }

    @Override
    public void executeMessageActions() {
        if (!messageResponses.isEmpty()) {
            messageResponses.forEach(message -> userContextInteractionEvent.reply(message).queue());
        }
    }

    @Override
    public UserContextInteractionEventMessageExecutor returnThis() {
        return this;
    }

    @Override
    public MessageChannel getChannel() {
        return userContextInteractionEvent.getMessageChannel();
    }

    @Override
    public Message getMessage() {
        return null;
    }
}