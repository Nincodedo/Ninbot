package dev.nincodedo.ninbot.common.command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;

@Component
public class CommandListener extends ListenerAdapter {

    private CommandParser commandParser;

    public CommandListener(CommandParser commandParser, List<AbstractCommand> commands,
            List<SlashCommand> slashCommands) {
        this.commandParser = commandParser;
        addCommands(commands);
        addSlashCommands(slashCommands);
    }

    private void addSlashCommands(List<SlashCommand> slashCommands) {
        commandParser.addSlashCommands(slashCommands);
    }

    private void addCommands(List<AbstractCommand> commands) {
        commandParser.registerAliases(commands);
        commandParser.addCommands(commands);
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        if (isNinbotMention(event)) {
            commandParser.parseEvent(event);
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent slashCommandEvent) {
        commandParser.parseEvent(slashCommandEvent);
    }

    private boolean isNinbotMention(PrivateMessageReceivedEvent event) {
        return !event.getAuthor().isBot()
                && event.getMessage().getContentStripped().toLowerCase().startsWith("@ninbot");
    }
}
