package com.nincraft.ninbot.util;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.core.entities.Emote;
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
        message.addReaction(Emoji.CHECK_MARK).queue();
    }

    public static void reactUnsuccessfulResponse(Message message) {
        message.addReaction(Emoji.CROSS_X).queue();
    }

    public static void reactUnknownResponse(Message message) {
        message.addReaction(Emoji.QUESTION_MARK).queue();
    }

    public static void addReaction(Message message, String emoji) {
        message.addReaction(emoji).queue();
    }

    public static void addReaction(Message message, Emote emoji) {
        message.addReaction(emoji).queue();
    }

    public static void sendMessage(MessageChannel channel, Message message) {
        channel.sendMessage(message).queue();
    }
}
