package dev.nincodedo.ninbot.common.command.message;

import dev.nincodedo.ninbot.common.command.AbstractCommandParser;
import dev.nincodedo.ninbot.common.logging.ServerLoggerFactory;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class MessageContextCommandParser extends AbstractCommandParser<MessageContextCommand,
        MessageContextInteractionEvent> {

    protected MessageContextCommandParser(ServerLoggerFactory serverLoggerFactory, ExecutorService executorService) {
        super(serverLoggerFactory, executorService);
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
