package dev.nincodedo.ninbot.common.command.message;

import dev.nincodedo.ninbot.common.command.Command;
import dev.nincodedo.ninbot.common.command.CommandType;
import dev.nincodedo.ninbot.common.message.MessageContextInteractionEventMessageExecutor;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface MessageContextCommand extends Command<MessageContextInteractionEvent> {
    @Override
    default CommandType getType() {
        return CommandType.MESSAGE;
    }

    default MessageExecutor execute(@NotNull MessageContextInteractionEvent event) {
        var messageExecutor = new MessageContextInteractionEventMessageExecutor(event);
        return execute(event, messageExecutor);
    }

    MessageExecutor execute(@NotNull MessageContextInteractionEvent event,
            @NotNull MessageContextInteractionEventMessageExecutor messageExecutor);
}
