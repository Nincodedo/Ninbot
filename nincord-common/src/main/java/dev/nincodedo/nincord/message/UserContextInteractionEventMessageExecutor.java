package dev.nincodedo.nincord.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class UserContextInteractionEventMessageExecutor extends MessageExecutor {

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
    public MessageChannel getChannel() {
        return userContextInteractionEvent.getMessageChannel();
    }

    @Override
    public Message getMessage() {
        return null;
    }
}
