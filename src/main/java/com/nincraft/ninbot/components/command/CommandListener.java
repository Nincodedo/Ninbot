package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.components.admin.AdminCommand;
import com.nincraft.ninbot.components.adventure.RollCommand;
import com.nincraft.ninbot.components.config.ConfigDao;
import com.nincraft.ninbot.components.event.EventCommand;
import com.nincraft.ninbot.components.event.EventDao;
import com.nincraft.ninbot.components.event.EventScheduler;
import com.nincraft.ninbot.components.fun.DabCommand;
import com.nincraft.ninbot.components.info.HelpCommand;
import com.nincraft.ninbot.components.info.ListCommand;
import com.nincraft.ninbot.components.info.StatsCommand;
import com.nincraft.ninbot.components.poll.PollCommand;
import com.nincraft.ninbot.components.subscribe.SubscribeCommand;
import com.nincraft.ninbot.components.subscribe.UnsubscribeCommand;
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

    @Autowired
    public CommandListener(CommandParser commandParser, EventDao eventDao, EventScheduler eventScheduler, ConfigDao configDao, List<String> roleBlackList) {
        this.commandParser = commandParser;
        addCommands(eventDao, eventScheduler, configDao, roleBlackList);
    }

    private void addCommands(EventDao eventDao, EventScheduler eventScheduler, ConfigDao configDao, List<String> roleBlackList) {
        commandParser.addCommand(new SubscribeCommand(roleBlackList));
        commandParser.addCommand(new UnsubscribeCommand(roleBlackList));
        commandParser.addCommand(new ListCommand(roleBlackList));
        commandParser.addCommand(new EventCommand(eventDao, eventScheduler));
        commandParser.addCommand(new StatsCommand(roleBlackList));
        commandParser.addCommand(new AdminCommand(configDao));
        commandParser.addCommand(new DabCommand());
        commandParser.addCommand(new PollCommand());
        commandParser.addCommand(new RollCommand());
        commandParser.addCommand(new HelpCommand(commandParser.getCommandHashMap()));
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
        return !event.getAuthor().isBot() && event.getMessage().getContentStripped().toLowerCase().startsWith("@ninbot");
    }
}
