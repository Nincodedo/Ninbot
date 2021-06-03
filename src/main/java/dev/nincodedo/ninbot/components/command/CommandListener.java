package dev.nincodedo.ninbot.components.command;

import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.stats.StatManager;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandListener extends ListenerAdapter {

    private CommandParser commandParser;
    private ComponentService componentService;
    private ConfigService configService;
    private StatManager statManager;

    public CommandListener(CommandParser commandParser, List<AbstractCommand> commands, List<SlashCommand> slashCommands,
            ComponentService componentService, ConfigService configService, StatManager statManager) {
        this.commandParser = commandParser;
        this.componentService = componentService;
        this.configService = configService;
        this.statManager = statManager;
        addCommands(commands);
        addSlashCommands(slashCommands);
    }

    private void addSlashCommands(List<SlashCommand> slashCommands) {
        commandParser.addSlashCommands(slashCommands);
    }

    private void addCommands(List<AbstractCommand> commands) {
        commandParser.registerAliases(commands);
        commandParser.addCommands(commands);
        commandParser.addCommand(new HelpCommand(commandParser.getCommandHashMap(), componentService, configService,
                statManager));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (isNinbotMention(event)) {
            commandParser.parseEvent(event);
        }
    }

    @Override
    public void onSlashCommand(SlashCommandEvent slashCommandEvent){
        commandParser.parseEvent(slashCommandEvent);
    }

    private boolean isNinbotMention(MessageReceivedEvent event) {
        return !event.getAuthor().isBot()
                && event.getMessage().getContentStripped().toLowerCase().startsWith("@ninbot");
    }
}
