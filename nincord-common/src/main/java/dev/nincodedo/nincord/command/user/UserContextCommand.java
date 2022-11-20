package dev.nincodedo.nincord.command.user;

import dev.nincodedo.nincord.command.Command;
import dev.nincodedo.nincord.command.CommandType;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.UserContextInteractionEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface UserContextCommand extends Command<UserContextInteractionEvent> {
    @Override
    default CommandType getType() {
        return CommandType.USER;
    }

    default MessageExecutor execute(@NotNull UserContextInteractionEvent event) {
        var messageExecutor = new UserContextInteractionEventMessageExecutor(event);
        return execute(event, messageExecutor);
    }

    MessageExecutor execute(@NotNull UserContextInteractionEvent event,
            @NotNull UserContextInteractionEventMessageExecutor messageExecutor);
}
