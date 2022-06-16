package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.BaseListenerAdapter;
import dev.nincodedo.ninbot.common.logging.ServerLogger;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandListener extends BaseListenerAdapter {

    private CommandParser commandParser;

    public CommandListener(CommandParser commandParser, List<Command> commands, ServerLogger serverLogger) {
        super(serverLogger);
        this.commandParser = commandParser;
        addCommands(commands);
    }

    private void addCommands(List<Command> commands) {
        commandParser.addCommands(commands);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        commandParser.parseEvent(slashCommandEvent);
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent messageContextInteractionEvent) {
        commandParser.parseEvent(messageContextInteractionEvent);
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent userContextInteractionEvent) {
        commandParser.parseEvent(userContextInteractionEvent);
    }

    @Override
    public void onCommandAutoCompleteInteraction(
            @NotNull CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent) {
        commandParser.parseEvent(commandAutoCompleteInteractionEvent);
    }
}
