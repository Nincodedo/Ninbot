package com.nincraft.ninbot.components.event;

import com.nincraft.ninbot.util.MessageUtils;
import com.nincraft.ninbot.util.Reference;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.JDA;

import java.util.TimerTask;

@Log4j2
class EventAnnounce extends TimerTask {

    private Event event;
    private JDA jda;
    private String announcementChannel;
    private int minutesBeforeStart;

    EventAnnounce(Event event, int minutesBeforeStart, JDA jda, boolean debugEnabled) {
        this.event = event;
        this.jda = jda;
        this.announcementChannel = debugEnabled ? Reference.OCW_DEBUG_CHANNEL : Reference.OCW_EVENT_ANNOUNCE_CHANNEL;
        this.minutesBeforeStart = minutesBeforeStart;
    }

    @Override
    public void run() {
        log.info("Running announce for event {}", event.getName());
        val ocwServer = jda.getGuildById(Reference.OCW_SERVER_ID);
        val channel = ocwServer.getTextChannelById(announcementChannel);
        val gameRoleId = ocwServer.getRolesByName(event.getGameName(), true).get(0);
        MessageUtils.sendMessage(channel, event.buildChannelMessage(gameRoleId.getId(), minutesBeforeStart));
    }
}
