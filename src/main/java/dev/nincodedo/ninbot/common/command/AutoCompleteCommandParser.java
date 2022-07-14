package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.logging.ServerLogger;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

public class AutoCompleteCommandParser extends AbstractCommandParser<AutoCompleteCommand,
        CommandAutoCompleteInteractionEvent> {

    protected AutoCompleteCommandParser(ServerLogger serverLogger) {
        super(serverLogger);
    }

    @Override
    public Class<AutoCompleteCommand> getCommandClass() {
        return AutoCompleteCommand.class;
    }

    @Override
    public Class<CommandAutoCompleteInteractionEvent> getEventClass() {
        return CommandAutoCompleteInteractionEvent.class;
    }

}
