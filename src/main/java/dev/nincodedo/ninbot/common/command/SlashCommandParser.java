package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
public class SlashCommandParser extends AbstractCommandParser<SlashCommand, SlashCommandInteractionEvent, SlashCommandParser> {

    @Override
    Class<SlashCommand> getCommandClass() {
        return SlashCommand.class;
    }

    @Override
    Class<SlashCommandInteractionEvent> getEventClass() {
        return SlashCommandInteractionEvent.class;
    }
}
