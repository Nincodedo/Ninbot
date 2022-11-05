package dev.nincodedo.ninbot.common.command.message;

import dev.nincodedo.ninbot.common.command.AbstractCommandParser;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class MessageContextCommandParser extends AbstractCommandParser<MessageContextCommand,
        MessageContextInteractionEvent> {

    protected MessageContextCommandParser(@Qualifier("commandParserThreadPool") ExecutorService commandExecutorService) {
        super(commandExecutorService);
    }

    @Override
    public Class<MessageContextCommand> getCommandClass() {
        return MessageContextCommand.class;
    }

    @Override
    public Class<MessageContextInteractionEvent> getEventClass() {
        return MessageContextInteractionEvent.class;
    }

}
