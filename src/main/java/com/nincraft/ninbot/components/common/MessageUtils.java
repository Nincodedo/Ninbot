package com.nincraft.ninbot.components.common;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public class MessageUtils {
    public void sendMessage(MessageChannel channel, String message, String... parameters) {
        if (parameters == null) {
            channel.sendMessage(message).queue();
        } else {
            channel.sendMessage(String.format(message, parameters)).queue();
        }
    }

    public void addReaction(Message message, String emoji) {
        message.addReaction(emoji).queue();
    }
}
