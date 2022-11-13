package dev.nincodedo.ninbot.common.command.user;

import dev.nincodedo.ninbot.common.command.Command;
import dev.nincodedo.ninbot.common.command.CommandType;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.UserContextInteractionEventMessageExecutor;
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
