package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.logging.ServerLoggerFactory;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;

import java.util.concurrent.ExecutorService;

public class AutoCompleteCommandParser extends AbstractCommandParser<AutoCompleteCommand,
        CommandAutoCompleteInteractionEvent> {

    protected AutoCompleteCommandParser(ServerLoggerFactory serverLoggerFactory, ExecutorService executorService) {
        super(serverLoggerFactory, executorService);
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
