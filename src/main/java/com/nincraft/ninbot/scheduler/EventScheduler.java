package com.nincraft.ninbot.scheduler;

import com.nincraft.ninbot.Ninbot;
import com.nincraft.ninbot.action.EventAnnounce;
import com.nincraft.ninbot.action.EventRemove;
import com.nincraft.ninbot.container.Event;
import com.nincraft.ninbot.dao.IEventDao;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
        Instant eventStartTime = event.getStartTime().atZone(ZoneId.systemDefault()).toInstant();
        Instant eventEarlyReminder = event.getStartTime().atZone(ZoneId.systemDefault()).toInstant().minus(10, ChronoUnit.MINUTES);
        Instant eventEndTime;
        if (event.getEndTime() != null) {
            eventEndTime = event.getStartTime().atZone(ZoneId.systemDefault()).toInstant();
        } else {
            eventEndTime = eventStartTime.plus(1, ChronoUnit.DAYS);
        }
        if (eventEndTime.isBefore(Instant.now())) {
            log.debug("Removing event {}, the end time is passed", event.getName());
            new EventRemove(event).run();
        } else {
            log.debug("Scheduling {} for {}", event.getName(), event.getStartTime());
            scheduleEvent(event, timer, eventStartTime);
            scheduleEvent(event, timer, eventEarlyReminder);
            timer.schedule(new EventRemove(event), Date.from(eventEndTime));
        }
    }

    private void scheduleEvent(Event event, Timer timer, Instant eventTime) {
        if (!eventTime.isBefore(Instant.now())) {
            timer.schedule(new EventAnnounce(event), Date.from(eventTime));
        }
    }
}
