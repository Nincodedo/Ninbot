package dev.nincodedo.ninbot.common.command.message;

import dev.nincodedo.ninbot.common.command.Command;
import dev.nincodedo.ninbot.common.command.CommandType;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;

public interface MessageContextCommand extends Command<MessageContextInteractionEvent> {
    @Override
    default CommandType getType() {
        return CommandType.MESSAGE;
    }
}
