package com.nincraft.ninbot.components.event;

import com.nincraft.ninbot.components.config.ConfigService;
import com.nincraft.ninbot.util.MessageUtils;
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
    private String announcementConfigName;

    EventAnnounce(Event event, int minutesBeforeStart, boolean debugEnabled, ConfigService configService, JDA jda) {
        this.event = event;
        this.minutesBeforeStart = minutesBeforeStart;
        this.announcementConfigName = debugEnabled ? "debugAnnouncementChannel" : "announcementChannel";
        this.configService = configService;
        this.jda = jda;
    }

    @Override
    public void run() {
        log.info("Running announce for event {}", event.getName());
        val serverId = event.getServerId();
        val announcementServer = jda.getGuildById(serverId);
        val config = configService.getConfigByName(serverId, announcementConfigName);
        if (config.isEmpty()) {
            log.error("Config not set for server {}, config name {}", serverId, announcementConfigName);
            return;
        }
        val channel = announcementServer.getTextChannelById(config.get(0).getValue());
        val gameRoleId = announcementServer.getRolesByName(event.getGameName(), true).get(0);
        MessageUtils.sendMessage(channel, event.buildChannelMessage(gameRoleId.getId(), minutesBeforeStart));
    }
}
