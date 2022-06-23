package dev.nincodedo.ninbot.common.command.user;

import dev.nincodedo.ninbot.common.command.AbstractCommandParser;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.springframework.stereotype.Component;

@Component
public class UserContextCommandParser extends AbstractCommandParser<UserContextCommand, UserContextInteractionEvent,
        UserContextCommandParser> {
    @Override
    public Class<UserContextCommand> getCommandClass() {
        return UserContextCommand.class;
    }

    @Override
    public Class<UserContextInteractionEvent> getEventClass() {
        return UserContextInteractionEvent.class;
    }

}
