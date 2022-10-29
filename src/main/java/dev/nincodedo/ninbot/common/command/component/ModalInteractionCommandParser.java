package dev.nincodedo.ninbot.common.command.component;

import dev.nincodedo.ninbot.common.command.AbstractCommandParser;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class ModalInteractionCommandParser extends AbstractCommandParser<ModalInteraction, ModalInteractionEvent> {

    protected ModalInteractionCommandParser(ExecutorService commandExecutorService) {
        super(commandExecutorService);
    }

    @Override
    public Class<ModalInteraction> getCommandClass() {
        return ModalInteraction.class;
    }

    @Override
    public Class<ModalInteractionEvent> getEventClass() {
        return ModalInteractionEvent.class;
    }
}
