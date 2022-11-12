package dev.nincodedo.ninbot.common.command.user;

import dev.nincodedo.ninbot.common.command.AbstractCommandParser;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class UserContextCommandParser extends AbstractCommandParser<UserContextCommand, UserContextInteractionEvent> {

    protected UserContextCommandParser(@Qualifier("commandParserThreadPool") ExecutorService commandExecutorService) {
        super(commandExecutorService);
    }

    @Override
    protected String getCommandName(UserContextInteractionEvent event) {
        return event.getName();
    }

    @Override
    public Class<UserContextCommand> getCommandClass() {
        return UserContextCommand.class;
    }

    @Override
    public Class<UserContextInteractionEvent> getEventClass() {
        return UserContextInteractionEvent.class;
    }
}
