package dev.nincodedo.nincord.command.message;

import dev.nincodedo.nincord.command.AbstractCommandParser;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.ExecutorService;

public class MessageContextCommandParser extends AbstractCommandParser<MessageContextCommand,
        MessageContextInteractionEvent> {

    public MessageContextCommandParser(@Qualifier("commandParserThreadPool") ExecutorService commandExecutorService) {
        super(commandExecutorService, MessageContextCommand.class, MessageContextInteractionEvent.class);
    }

    @Override
    protected String getCommandName(MessageContextInteractionEvent event) {
        return event.getName();
    }
}
