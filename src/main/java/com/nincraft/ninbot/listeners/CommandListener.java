package com.nincraft.ninbot.listeners;

import com.nincraft.ninbot.command.*;
import com.nincraft.ninbot.command.util.CommandParser;
import com.nincraft.ninbot.dao.IEventDao;
import com.nincraft.ninbot.scheduler.EventScheduler;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

    private CommandParser commandParser;
    private boolean debugEnabled;

    public CommandListener(IEventDao eventDao, EventScheduler eventScheduler, boolean debugEnabled) {
        commandParser = new CommandParser();
        this.debugEnabled = debugEnabled;
        commandParser.addCommand(new SubscribeCommand());
        commandParser.addCommand(new UnsubscribeCommand());
        commandParser.addCommand(new ListCommand());
        commandParser.addCommand(new HelpCommand());
        commandParser.addCommand(new EventCommand(eventDao, eventScheduler));
        commandParser.addCommand(new StatsCommand());
        commandParser.addCommand(new AdminCommand());
        commandParser.addCommand(new DabCommand());
        commandParser.addCommand(new PollCommand());
        commandParser.addCommand(new RollCommand());
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
        return !event.getAuthor().isBot() && event.getMessage().getContent().toLowerCase().startsWith("@ninbot");
    }
}
