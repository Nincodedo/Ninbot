package com.nincraft.ninbot.action;

import com.nincraft.ninbot.Ninbot;
import com.nincraft.ninbot.entity.Event;
import com.nincraft.ninbot.util.MessageSenderHelper;
import com.nincraft.ninbot.util.Reference;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.JDA;

import java.util.TimerTask;

@Log4j2
public class EventAnnounce extends TimerTask {

    private Event event;
    private JDA jda;
    private String announcementChannel;
    private int minutesBeforeStart;

    public EventAnnounce(Event event, int minutesBeforeStart) {
        this.event = event;
        this.jda = Ninbot.getJda();
        this.announcementChannel = Ninbot.isDebugEnabled() ? Reference.OCW_DEBUG_CHANNEL : Reference.OCW_EVENT_ANNOUNCE_CHANNEL;
        this.minutesBeforeStart = minutesBeforeStart;
    }

    @Override
    public void run() {
        log.info("Running announce for event {}", event.getName());
        val ocwServer = jda.getGuildById(Reference.OCW_SERVER_ID);
        val channel = ocwServer.getTextChannelById(announcementChannel);
        val gameRoleId = ocwServer.getRolesByName(event.getGameName(), true).get(0);
        MessageSenderHelper.sendMessage(channel, event.buildChannelMessage(gameRoleId.getId(), minutesBeforeStart));
    }
}
