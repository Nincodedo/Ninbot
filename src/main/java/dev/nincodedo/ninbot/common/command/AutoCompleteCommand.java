package dev.nincodedo.ninbot.common.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public interface AutoCompleteCommand extends Command<CommandAutoCompleteInteractionEvent> {
    @Override
    default boolean isAbleToRegisterOnGuild() {
        return false;
    }

    @Override
    default CommandType getType() {
        return CommandType.AUTO_COMPLETE;
    }
}
