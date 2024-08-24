package dev.nincodedo.nincord.command.user;

import dev.nincodedo.nincord.command.AbstractCommandParser;
import dev.nincodedo.nincord.command.CommandMetrics;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.ExecutorService;

public class UserContextCommandParser extends AbstractCommandParser<UserContextCommand, UserContextInteractionEvent> {

    public UserContextCommandParser(@Qualifier("commandParserThreadPool") ExecutorService commandExecutorService,
            CommandMetrics commandMetrics) {
        super(commandExecutorService, UserContextCommand.class, UserContextInteractionEvent.class, commandMetrics);
    }

    @Override
    protected String getCommandName(UserContextInteractionEvent event) {
        return event.getName();
    }
}
