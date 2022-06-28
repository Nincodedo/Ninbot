package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.message.AutoCompleteCommandMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public interface AutoCompleteCommand extends Command<AutoCompleteCommandMessageExecutor, CommandAutoCompleteInteractionEvent> {
}
