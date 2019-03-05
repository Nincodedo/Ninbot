package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.components.info.HelpCommand;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandListener extends ListenerAdapter {

    private CommandParser commandParser;

    @Autowired
    public CommandListener(CommandParser commandParser, List<AbstractCommand> commands) {
        this.commandParser = commandParser;
        addCommands(commands);
    }

    private void addCommands(List<AbstractCommand> commands) {
        commandParser.registerAliases(commands);
        commandParser.addCommands(commands);
        commandParser.addCommand(new HelpCommand(commandParser.getCommandHashMap()));
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
