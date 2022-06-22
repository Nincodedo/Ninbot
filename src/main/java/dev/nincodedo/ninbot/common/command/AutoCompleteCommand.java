package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public interface AutoCompleteCommand extends Command<SlashCommandEventMessageExecutor, CommandAutoCompleteInteractionEvent> {
    String getName();

    void autoComplete(CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent);
}
