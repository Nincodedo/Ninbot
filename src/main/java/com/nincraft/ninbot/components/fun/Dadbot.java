package com.nincraft.ninbot.components.fun;

import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import com.nincraft.ninbot.components.config.component.ComponentService;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Dadbot extends ListenerAdapter {

    private Random random;
    private ConfigService configService;
    private ComponentService componentService;
    private String componentName;

    @Autowired
    public Dadbot(ConfigService configService, ComponentService componentService) {
        random = new Random();
        this.configService = configService;
        componentName = "dad";
        this.componentService = componentService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()
                && !componentService.isDisabled(componentName, event.getGuild().getId())) {
            parseMessage(event);
        }
    }

    private void parseMessage(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        val first = message.split("\\s+")[0];
        if (!(first.equalsIgnoreCase("I'm") || first.equalsIgnoreCase("im")) || (!event.getChannelType().isGuild())) {
            return;
        }
        if (StringUtils.isNotBlank(message) && message.split("\\s+").length >= 1 && checkChance()) {
            hiImDad(message, event);
        }
    }

    private boolean channelIsBlacklisted(String serverId, String channelId) {
        val channelConfigList = configService.getConfigByName(serverId, ConfigConstants.DADBOT_BLACKLIST_CHANNEL);
        return channelConfigList.stream().anyMatch(config -> config.getValue().equals(channelId));
    }

    private void hiImDad(String message, MessageReceivedEvent event) {
        if (!channelIsBlacklisted(event.getGuild().getId(), event.getChannel().getId())) {
            return;
        }
        String stringBuilder = "Hi" +
                message.substring(message.indexOf(' ')) +
                ", I'm Dad!";
        event.getChannel().sendMessage(stringBuilder).queue();
    }

    private boolean checkChance() {
        return random.nextInt(100) < 10;
    }
}
