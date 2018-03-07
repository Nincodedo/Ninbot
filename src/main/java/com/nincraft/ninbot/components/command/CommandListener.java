package com.nincraft.ninbot.components.command;

import com.nincraft.ninbot.components.admin.AdminCommand;
import com.nincraft.ninbot.components.adventure.RollCommand;
import com.nincraft.ninbot.components.dab.DabCommand;
import com.nincraft.ninbot.components.event.EventCommand;
import com.nincraft.ninbot.components.event.EventScheduler;
import com.nincraft.ninbot.components.event.IEventDao;
import com.nincraft.ninbot.components.info.HelpCommand;
import com.nincraft.ninbot.components.info.ListCommand;
import com.nincraft.ninbot.components.info.StatsCommand;
import com.nincraft.ninbot.components.poll.PollCommand;
import com.nincraft.ninbot.components.subscribe.SubscribeCommand;
import com.nincraft.ninbot.components.subscribe.UnsubscribeCommand;
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
