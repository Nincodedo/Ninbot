package com.nincraft.ninbot.util;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

@UtilityClass
public class MessageUtils {
    public static void sendMessage(MessageChannel channel, String message, String... parameters) {
        if (parameters == null) {
            channel.sendMessage(message).queue();
        } else {
            channel.sendMessage(String.format(message, parameters)).queue();
        }
    }

    public static void reactSuccessfulResponse(Message message) {
        message.addReaction("\u2705").queue();
    }

    public static void reactUnknownResponse(Message message) {
        message.addReaction("\u2274").queue();
    }
}
