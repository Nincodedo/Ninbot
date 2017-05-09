package com.nincraft.ninbot.events;

import com.nincraft.ninbot.util.CommandParser;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (isNinbotMention(event)) {
            new CommandParser().parseEvent(event);
        }
    }

    private boolean isNinbotMention(MessageReceivedEvent event) {
        return !event.getAuthor().isBot() && event.getMessage().getContent().startsWith("@Ninbot");
    }
}
