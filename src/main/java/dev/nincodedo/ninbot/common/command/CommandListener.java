package dev.nincodedo.ninbot.common.command;

import dev.nincodedo.ninbot.common.BaseListenerAdapter;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandListener extends BaseListenerAdapter {

    private List<AbstractCommandParser> commandParsers;

    public CommandListener(List<AbstractCommandParser> commandParsers,
            List<Command> commands) {
        this.commandParsers = commandParsers;
        addCommands(commands);
    }

    private void addCommands(List<Command> commands) {
        commands.forEach(this::addCommand);
    }

    private void addCommand(Command command) {
        commandParsers.stream()
                .filter(commandParser -> commandParser.getCommandClass().isInstance(command))
                .forEach(commandParser -> commandParser.addCommand(command));
    }

    private AbstractCommandParser getCommandParserForEvent(GenericCommandInteractionEvent event) {
        return commandParsers.stream()
                .filter(abstractCommandParser -> abstractCommandParser.isEventMatchParser(event))
                .findFirst()
                .get();
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        getCommandParserForEvent(event).parseEvent(event);
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        getCommandParserForEvent(event).parseEvent(event);
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        getCommandParserForEvent(event).parseEvent(event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(
            @NotNull CommandAutoCompleteInteractionEvent event) {
        //getCommandParserForEvent(event).parseEvent(event);
    }
}
