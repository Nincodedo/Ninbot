package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.components.config.ConfigService;
import com.nincraft.ninbot.components.config.component.ComponentService;
import com.nincraft.ninbot.components.info.HelpCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandListener extends ListenerAdapter {

    private CommandParser commandParser;
    private ComponentService componentService;
    private ConfigService configService;

    public CommandListener(CommandParser commandParser, List<AbstractCommand> commands,
            ComponentService componentService, ConfigService configService) {
        this.commandParser = commandParser;
        this.componentService = componentService;
        this.configService = configService;
        addCommands(commands);
    }

    private void addCommands(List<AbstractCommand> commands) {
        commandParser.registerAliases(commands);
        commandParser.addCommands(commands);
        commandParser.addCommand(new HelpCommand(commandParser.getCommandHashMap(), componentService, configService));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (isNinbotMention(event)) {
            commandParser.parseEvent(event);
        }
    }

    private boolean isNinbotMention(MessageReceivedEvent event) {
        return !event.getAuthor().isBot()
                && event.getMessage().getContentStripped().toLowerCase().startsWith("@ninbot");
    }
}
