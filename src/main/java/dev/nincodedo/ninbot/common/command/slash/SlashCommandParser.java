package dev.nincodedo.ninbot.common.command.slash;

import dev.nincodedo.ninbot.common.command.AbstractCommandParser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
public class SlashCommandParser extends AbstractCommandParser<SlashCommand, SlashCommandInteractionEvent> {

    @Override
    public Class<SlashCommand> getCommandClass() {
        return SlashCommand.class;
    }

    @Override
    public Class<SlashCommandInteractionEvent> getEventClass() {
        return SlashCommandInteractionEvent.class;
    }

}
