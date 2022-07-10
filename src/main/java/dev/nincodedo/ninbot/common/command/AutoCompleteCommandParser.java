package dev.nincodedo.ninbot.common.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public class AutoCompleteCommandParser extends AbstractCommandParser<AutoCompleteCommand,
        CommandAutoCompleteInteractionEvent> {
    @Override
    public Class<AutoCompleteCommand> getCommandClass() {
        return AutoCompleteCommand.class;
    }

    @Override
    public Class<CommandAutoCompleteInteractionEvent> getEventClass() {
        return CommandAutoCompleteInteractionEvent.class;
    }

}
