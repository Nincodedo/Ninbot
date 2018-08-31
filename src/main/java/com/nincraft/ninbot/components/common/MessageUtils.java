package com.nincraft.ninbot.components.common;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.springframework.stereotype.Component;

@Component
public class MessageUtils {

    private static final String CHECK_MARK = "\u2705";
    private static final String QUESTION_MARK = "\u2754";
    private static final String CROSS_X = "\u274C";

    public void sendMessage(MessageChannel channel, String message, String... parameters) {
        if (parameters == null) {
            channel.sendMessage(message).queue();
        } else {
            channel.sendMessage(String.format(message, parameters)).queue();
        }
    }

    public void reactSuccessfulResponse(Message message) {
        message.addReaction(CHECK_MARK).queue();
    }

    public void reactUnsuccessfulResponse(Message message) {
        message.addReaction(CROSS_X).queue();
    }

    public void reactUnknownResponse(Message message) {
        message.addReaction(QUESTION_MARK).queue();
    }

    public void addReaction(Message message, String emoji) {
        message.addReaction(emoji).queue();
    }

    public void addReaction(Message message, Emote emoji) {
        message.addReaction(emoji).queue();
    }

    public void sendMessage(MessageChannel channel, Message message) {
        channel.sendMessage(message).queue();
    }

    public void reactAccordingly(Message message, boolean isSuccessful) {
        if (isSuccessful) {
            reactSuccessfulResponse(message);
        } else {
            reactUnsuccessfulResponse(message);
        }
    }

    public void sendPrivateMessage(User user, Message message) {
        user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue());
    }

    public void sendPrivateMessage(User user, String message) {
        user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue());
    }
}
