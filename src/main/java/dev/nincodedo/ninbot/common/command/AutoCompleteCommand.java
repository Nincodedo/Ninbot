package dev.nincodedo.ninbot.common.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public interface AutoCompleteCommand {
    String getName();

    void autoComplete(CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent);
}
