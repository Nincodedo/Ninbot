package dev.nincodedo.nincord.command.component;

import dev.nincodedo.nincord.command.AbstractCommandParser;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.ExecutorService;

public class ButtonInteractionCommandParser extends AbstractCommandParser<ButtonInteraction,
        ButtonInteractionEvent> {

    public ButtonInteractionCommandParser(@Qualifier("commandParserThreadPool") ExecutorService commandExecutorService) {
        super(commandExecutorService, ButtonInteraction.class, ButtonInteractionEvent.class);
    }

    @Override
    protected String getCommandName(ButtonInteractionEvent event) {
        return getComponentName(event.getComponentId());
    }
}
