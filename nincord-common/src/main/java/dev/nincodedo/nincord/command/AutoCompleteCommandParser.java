package dev.nincodedo.nincord.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.ExecutorService;

public class AutoCompleteCommandParser extends AbstractCommandParser<AutoCompleteCommand,
        CommandAutoCompleteInteractionEvent> {

    public AutoCompleteCommandParser(@Qualifier("commandParserThreadPool") ExecutorService commandExecutorService,
            CommandMetrics commandMetrics) {
        super(commandExecutorService, AutoCompleteCommand.class, CommandAutoCompleteInteractionEvent.class,
                commandMetrics);
    }

    @Override
    protected String getCommandName(CommandAutoCompleteInteractionEvent event) {
        return event.getName();
    }
}
