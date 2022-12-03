package dev.nincodedo.nincord.command.component;

import dev.nincodedo.nincord.command.AbstractCommandParser;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class ModalInteractionCommandParser extends AbstractCommandParser<ModalInteraction, ModalInteractionEvent> {

    protected ModalInteractionCommandParser(@Qualifier("commandParserThreadPool") ExecutorService commandExecutorService) {
        super(commandExecutorService);
    }

    @Override
    protected String getCommandName(ModalInteractionEvent event) {
        return getComponentName(event.getModalId());
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
