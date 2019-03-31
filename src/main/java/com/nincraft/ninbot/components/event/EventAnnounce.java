package com.nincraft.ninbot.components.event;

import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.JDA;

import java.util.TimerTask;

@Log4j2
class EventAnnounce extends TimerTask {

    private Event event;
    private JDA jda;
    private int minutesBeforeStart;
    private ConfigService configService;

    EventAnnounce(Event event, int minutesBeforeStart, ConfigService configService, JDA jda) {
        this.event = event;
        this.minutesBeforeStart = minutesBeforeStart;
        this.configService = configService;
        this.jda = jda;
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
            channel.sendMessage(event.buildChannelMessage(gameRoleId.getId(), minutesBeforeStart)).queue();
        }
    }
}
