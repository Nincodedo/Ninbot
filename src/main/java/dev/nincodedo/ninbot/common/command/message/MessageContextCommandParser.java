package dev.nincodedo.ninbot.common.command.message;

import dev.nincodedo.ninbot.common.command.AbstractCommandParser;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.springframework.stereotype.Component;

@Component
public class MessageContextCommandParser extends AbstractCommandParser<MessageContextCommand,
        MessageContextInteractionEvent, MessageContextCommandParser> {
    @Override
    public Class<MessageContextCommand> getCommandClass() {
        return MessageContextCommand.class;
    }

    @Override
    public Class<MessageContextInteractionEvent> getEventClass() {
        return MessageContextInteractionEvent.class;
    }

}
