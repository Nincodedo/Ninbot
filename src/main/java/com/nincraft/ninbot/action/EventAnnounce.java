package com.nincraft.ninbot.action;

import com.nincraft.ninbot.Ninbot;
import com.nincraft.ninbot.container.Event;
import com.nincraft.ninbot.util.MessageSenderHelper;
import com.nincraft.ninbot.util.Reference;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.JDA;

import java.util.TimerTask;

@Log4j2
public class EventAnnounce extends TimerTask {

    private Event event;
    private JDA jda = Ninbot.getJda();

    public EventAnnounce(Event event) {
        this.event = event;
    }

    @Override
    public void run() {
        log.info("Running announce for event {}", event.getName());
        val ocwServer = jda.getGuildById(Reference.OCW_SERVER_ID);
        val channel = ocwServer.getTextChannelById(Reference.OCW_EVENT_ANNOUNCE_CHANNEL);
        MessageSenderHelper.sendMessage(channel, event.buildChannelMessage());
    }
}
