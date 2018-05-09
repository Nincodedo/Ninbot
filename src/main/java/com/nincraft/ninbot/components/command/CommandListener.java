package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.components.admin.AdminCommand;
import com.nincraft.ninbot.components.adventure.RollCommand;
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

@Component
public class CommandListener extends ListenerAdapter {

    private CommandParser commandParser;
    @Value("${debugEnabled}")
    private boolean debugEnabled;

    @Autowired
    public CommandListener(EventDao eventDao, EventScheduler eventScheduler) {
        commandParser = new CommandParser();
        commandParser.addCommand(new SubscribeCommand());
        commandParser.addCommand(new UnsubscribeCommand());
        commandParser.addCommand(new ListCommand());
        commandParser.addCommand(new EventCommand(eventDao, eventScheduler));
        commandParser.addCommand(new StatsCommand());
        commandParser.addCommand(new AdminCommand());
        commandParser.addCommand(new DabCommand());
        commandParser.addCommand(new PollCommand());
        commandParser.addCommand(new RollCommand());
        commandParser.addCommand(new HelpCommand(commandParser));
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
