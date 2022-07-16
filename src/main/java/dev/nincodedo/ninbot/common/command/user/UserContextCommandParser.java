package dev.nincodedo.ninbot.common.command.user;

import dev.nincodedo.ninbot.common.command.AbstractCommandParser;
import dev.nincodedo.ninbot.common.logging.ServerLoggerFactory;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class UserContextCommandParser extends AbstractCommandParser<UserContextCommand, UserContextInteractionEvent> {

    protected UserContextCommandParser(ServerLoggerFactory serverLoggerFactory, ExecutorService executorService) {
        super(serverLoggerFactory, executorService);
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
