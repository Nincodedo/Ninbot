package com.nincraft.ninbot.components.common;

import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import net.dv8tion.jda.core.events.channel.text.update.TextChannelUpdateTopicEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LocaleService {

    private ConfigService configService;
    private Locale defaultLocale;

    public LocaleService(ConfigService configService) {
        this.configService = configService;
        defaultLocale = Locale.ENGLISH;
    }

    public Locale getLocale(MessageReceivedEvent event) {
        if (event.getChannelType().isGuild()) {
            return configService.getConfigByName(event.getGuild().getId(),
                    ConfigConstants.SERVER_LOCALE).stream().filter(config ->
                    config.getServerId().equals(event.getGuild().getId()))
                    .findFirst().map(config ->
                            new Locale(config.getValue())).orElse(defaultLocale);
        } else {
            return defaultLocale;
        }
    }

    public Locale getLocale(TextChannelUpdateTopicEvent event) {
        return configService.getConfigByName(event.getGuild().getId(),
                ConfigConstants.SERVER_LOCALE).stream().filter(config ->
                config.getServerId().equals(event.getGuild().getId()))
                .findFirst().map(config ->
                        new Locale(config.getValue())).orElse(defaultLocale);
    }

    public Locale getLocale(String serverId) {
        return configService.getConfigByName(serverId,
                ConfigConstants.SERVER_LOCALE).stream().filter(config ->
                config.getServerId().equals(serverId))
                .findFirst().map(config ->
                        new Locale(config.getValue())).orElse(defaultLocale);
    }
}
