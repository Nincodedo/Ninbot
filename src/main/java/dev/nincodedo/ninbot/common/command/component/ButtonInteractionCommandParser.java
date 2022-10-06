package dev.nincodedo.ninbot.common.command.component;

import dev.nincodedo.ninbot.common.command.AbstractCommandParser;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class ButtonInteractionCommandParser extends AbstractCommandParser<ButtonInteraction,
        ButtonInteractionEvent> {

    protected ButtonInteractionCommandParser(ExecutorService commandExecutorService) {
        super(commandExecutorService);
    }

    @Override
    public Class<ButtonInteraction> getCommandClass() {
        return ButtonInteraction.class;
    }

    @Override
    public Class<ButtonInteractionEvent> getEventClass() {
        return ButtonInteractionEvent.class;
    }
}