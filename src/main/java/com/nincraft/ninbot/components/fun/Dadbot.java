package com.nincraft.ninbot.components.fun;

import com.nincraft.ninbot.util.MessageUtils;
import lombok.val;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Dadbot extends ListenerAdapter {

    private Random random;

    public Dadbot() {
        random = new Random();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            parseMessage(event);
        }
    }

    private void parseMessage(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        if (StringUtils.isNotBlank(message) && message.split(" ").length >= 1) {
            String first = message.split(" ")[0];
            if ((first.equalsIgnoreCase("I'm") || first.equalsIgnoreCase("im")) && checkChance()) {
                hiImDad(message, event.getChannel());
            }
        }
    }

    private void hiImDad(String message, MessageChannel channel) {
        String stringBuilder = "Hi" +
                message.substring(message.indexOf(' '), message.length()) +
                ", I'm Dad!";
        MessageUtils.sendMessage(channel, stringBuilder);
    }

    private boolean checkChance() {
        return random.nextInt(100) <= 10;
    }
}
