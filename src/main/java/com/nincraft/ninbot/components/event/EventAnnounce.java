package com.nincraft.ninbot.components.event;

import com.nincraft.ninbot.components.common.LocaleService;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.JDA;

import java.util.ResourceBundle;
import java.util.TimerTask;

@Log4j2
class EventAnnounce extends TimerTask {

    private Event event;
    private JDA jda;
    private int minutesBeforeStart;
    private ConfigService configService;
    private LocaleService localeService;

    EventAnnounce(Event event, int minutesBeforeStart, ConfigService configService, JDA jda,
            LocaleService localeService) {
        this.event = event;
        this.minutesBeforeStart = minutesBeforeStart;
        this.configService = configService;
        this.jda = jda;
        this.localeService = localeService;
    }

    @Override
    public void run() {
        log.info("Running announce for event {}", event.getName());
        val serverId = event.getServerId();
        val announcementServer = jda.getGuildById(serverId);
        val config = configService.getSingleValueByName(serverId, ConfigConstants.ANNOUNCE_CHANNEL);
        if (config.isPresent()) {
            val channel = announcementServer.getTextChannelById(config.get());
            val gameRoleId = announcementServer.getRolesByName(event.getGameName(), true).get(0);
            event.setResourceBundle(ResourceBundle.getBundle("lang", localeService.getLocale(serverId)));
            channel.sendMessage(event.buildChannelMessage(gameRoleId.getId(), minutesBeforeStart)).queue();
        }
    }
}
