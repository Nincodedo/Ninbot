package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.components.common.MessageUtils;
import com.nincraft.ninbot.components.info.HelpCommand;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandListener extends ListenerAdapter {

    private CommandParser commandParser;
    @Value("${debugEnabled:false}")
    private boolean debugEnabled;
    private MessageUtils messageUtils;

    @Autowired
    public CommandListener(CommandParser commandParser, List<AbstractCommand> commands, MessageUtils messageUtils) {
        this.commandParser = commandParser;
        this.messageUtils = messageUtils;
        addCommands(commands);
    }

    private void addCommands(List<AbstractCommand> commands) {
        commandParser.addCommands(commands);
        commandParser.addCommand(new HelpCommand(commandParser.getCommandHashMap(), messageUtils));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (isNinbotMention(event) && checkDebug(event.getChannel().getName())) {
            commandParser.parseEvent(event);
        }
    }

    private boolean checkDebug(String channelName) {
        return !debugEnabled || channelName.equals("admin-test");
    }

    private boolean isNinbotMention(MessageReceivedEvent event) {
        return !event.getAuthor().isBot()
                && event.getMessage().getContentStripped().toLowerCase().startsWith("@ninbot");
    }
}
