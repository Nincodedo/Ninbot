package dev.nincodedo.nincord.command.component;

import dev.nincodedo.nincord.command.AbstractCommandParser;
import dev.nincodedo.nincord.command.CommandMetrics;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.ExecutorService;

public class ModalInteractionCommandParser extends AbstractCommandParser<ModalInteraction, ModalInteractionEvent> {

    public ModalInteractionCommandParser(@Qualifier("commandParserThreadPool") ExecutorService commandExecutorService
            , CommandMetrics commandMetrics) {
        super(commandExecutorService, ModalInteraction.class, ModalInteractionEvent.class, commandMetrics);
    }

    @Override
    protected String getCommandName(ModalInteractionEvent event) {
        return getComponentName(event.getModalId());
    }
}
