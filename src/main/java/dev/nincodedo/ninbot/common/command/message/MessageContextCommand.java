package dev.nincodedo.ninbot.common.command.message;

import dev.nincodedo.ninbot.common.command.Command;
import dev.nincodedo.ninbot.common.command.CommandType;
import dev.nincodedo.ninbot.common.message.MessageContextInteractionEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

public interface MessageContextCommand extends Command<MessageContextInteractionEventMessageExecutor,
        MessageContextInteractionEvent> {
    @Override
    default CommandType getType() {
        return CommandType.MESSAGE;
    }
}
