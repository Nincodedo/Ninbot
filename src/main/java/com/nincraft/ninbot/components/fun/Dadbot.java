package com.nincraft.ninbot.components.fun;

import com.nincraft.ninbot.components.common.LocaleService;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import com.nincraft.ninbot.components.config.component.ComponentService;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

@Component
public class Dadbot extends ListenerAdapter {

    private Random random;
    private ConfigService configService;
    private ComponentService componentService;
    private String componentName;
    private LocaleService localeService;
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", Locale.ENGLISH);

    @Autowired
    public Dadbot(ConfigService configService, ComponentService componentService, LocaleService localeService) {
        random = new Random();
        this.configService = configService;
        componentName = "dad";
        this.componentService = componentService;
        this.localeService = localeService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()
                && !componentService.isDisabled(componentName, event.getGuild().getId())) {
            resourceBundle = ResourceBundle.getBundle("lang", localeService.getLocale(event.getGuild().getId()));
            parseMessage(event);
        }
    }

    private void parseMessage(MessageReceivedEvent event) {
        val message = event.getMessage().getContentStripped();
        val first = message.split("\\s+")[0];
        if (!(first.equalsIgnoreCase(resourceBundle.getString("listener.dad.imcontraction")) || first.equalsIgnoreCase(resourceBundle.getString("listener.dad.imnocontraction"))) || (!event.getChannelType().isGuild())) {
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
        String stringBuilder = resourceBundle.getString("listener.dad.hi") +
                message.substring(message.indexOf(' ')) +
                resourceBundle.getString("listener.dad.imdad");
        event.getChannel().sendMessage(stringBuilder).queue();
    }

    private boolean checkChance() {
        return random.nextInt(100) < 10;
    }
}
