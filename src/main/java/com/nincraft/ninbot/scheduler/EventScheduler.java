package com.nincraft.ninbot.scheduler;

import com.nincraft.ninbot.Ninbot;
import com.nincraft.ninbot.action.EventAnnounce;
import com.nincraft.ninbot.container.Event;
import com.nincraft.ninbot.dao.IEventDao;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;

@Log4j2
public class EventScheduler {

    private IEventDao eventDao;

    public EventScheduler() {
        eventDao = Ninbot.getEventDao();
    }

    public void scheduleAll() {
        log.trace("scheduling events");
        for (val event : eventDao.getAllEvents()) {
            scheduleEvent(event);
        }
    }

    public void addEvent(Event event) {
        eventDao.addEvent(event);
        scheduleEvent(event);
    }

    private void scheduleEvent(Event event) {
        Timer timer = new Timer();
        Instant instant = event.getStartTime().atZone(ZoneId.systemDefault()).toInstant();
        log.debug("scheduling {} for {}", event.getName(), event.getStartTime());
        timer.schedule(new EventAnnounce(event), Date.from(instant));
    }
}
