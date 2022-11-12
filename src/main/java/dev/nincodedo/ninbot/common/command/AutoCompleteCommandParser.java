package dev.nincodedo.ninbot.common.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class AutoCompleteCommandParser extends AbstractCommandParser<AutoCompleteCommand,
        CommandAutoCompleteInteractionEvent> {

    protected AutoCompleteCommandParser(@Qualifier("commandParserThreadPool") ExecutorService commandExecutorService) {
        super(commandExecutorService);
    }

    @Override
    protected String getCommandName(CommandAutoCompleteInteractionEvent event) {
        return event.getName();
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
