package com.nincraft.ninbot.listeners;

import com.nincraft.ninbot.util.CommandParser;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

    private CommandParser commandParser;

    public CommandListener() {
        commandParser = new CommandParser();
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (isNinbotMention(event) && event.getChannel().getName().equals("admin-test")) {
            commandParser.parseEvent(event);
        }
    }

    private boolean isNinbotMention(MessageReceivedEvent event) {
        return !event.getAuthor().isBot() && event.getMessage().getContent().toLowerCase().startsWith("@ninbot");
    }
}
