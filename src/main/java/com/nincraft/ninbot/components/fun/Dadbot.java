package com.nincraft.ninbot.components.fun;

import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import com.nincraft.ninbot.util.MessageUtils;
import lombok.val;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Dadbot extends ListenerAdapter {

    private Random random;
    private ConfigService configService;

    @Autowired
    public Dadbot(ConfigService configService) {
        random = new Random();
        this.configService = configService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            parseMessage(event);
        }
    }

    private void parseMessage(MessageReceivedEvent event) {
        if (channelIsBlacklisted(event.getGuild().getId(), event.getChannel().getId())) {
            return;
        }
        val message = event.getMessage().getContentStripped();
        if (StringUtils.isNotBlank(message) && message.split(" ").length >= 1) {
            String first = message.split(" ")[0];
            if ((first.equalsIgnoreCase("I'm") || first.equalsIgnoreCase("im")) && checkChance()) {
                hiImDad(message, event.getChannel());
            }
        }
    }

    private boolean channelIsBlacklisted(String serverId, String channelId) {
        val channelConfigList = configService.getConfigByName(serverId, ConfigConstants.DADBOT_BLACKLIST_CHANNEL);
        return channelConfigList.stream().anyMatch(config -> config.getValue().equals(channelId));
    }

    private void hiImDad(String message, MessageChannel channel) {
        String stringBuilder = "Hi" +
                message.substring(message.indexOf(' ')) +
                ", I'm Dad!";
        MessageUtils.sendMessage(channel, stringBuilder);
    }

    private boolean checkChance() {
        return random.nextInt(100) < 10;
    }
}
