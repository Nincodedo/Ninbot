package com.nincraft.ninbot.events;

import lombok.val;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot() && event.getMessage().getContent().startsWith("@Ninbot") && "Nincodedo".equals(event.getAuthor().getName())) {
            val channel = event.getChannel();
            channel.sendMessage("new api shenanigans").queue();
        }
    }
}
