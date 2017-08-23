package com.nincraft.ninbot.util;

import lombok.experimental.UtilityClass;
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
}
