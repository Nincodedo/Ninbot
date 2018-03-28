package com.nincraft.ninbot.components.event;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.JDA;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Timer;

@Log4j2
public class EventScheduler {

    private EventDao eventDao;

    private JDA jda;

    private boolean debugEnabled;

    public EventScheduler(JDA jda, EventDao eventDao, boolean debugEnabled) {
        this.eventDao = eventDao;
        this.jda = jda;
        this.debugEnabled = debugEnabled;
    }

    public void scheduleAll() {
        log.trace("scheduling events");
        val events = eventDao.getAllEvents();
        for (val event : events) {
            scheduleEvent(event);
        }
    }

    public void addEvent(Event event) {
        eventDao.addEvent(event);
        scheduleEvent(event);
    }

    private void scheduleEvent(Event event) {
        Timer timer = new Timer();
        Instant eventStartTime = event.getStartTime().toInstant();
        int minutesBeforeStart = 30;
        Instant eventEarlyReminder = event.getStartTime().toInstant().minus(minutesBeforeStart, ChronoUnit.MINUTES);
        Instant eventEndTime;
        if (event.getEndTime() != null) {
            eventEndTime = event.getStartTime().toInstant();
        } else {
            eventEndTime = eventStartTime.plus(1, ChronoUnit.DAYS);
        }
        if (eventEndTime.isBefore(Instant.now())) {
            log.debug("Removing event {}, the end time is passed", event.getName());
            new EventRemove(event, eventDao).run();
        } else {
            log.debug("Scheduling {} for {}", event.getName(), event.getStartTime());
            scheduleEvent(event, timer, eventStartTime, 0);
            scheduleEvent(event, timer, eventEarlyReminder, minutesBeforeStart);
            timer.schedule(new EventRemove(event, eventDao), Date.from(eventEndTime));
        }
    }

    private void scheduleEvent(Event event, Timer timer, Instant eventTime, int minutesBeforeStart) {
        if (!eventTime.isBefore(Instant.now())) {
            timer.schedule(new EventAnnounce(event, minutesBeforeStart, jda, debugEnabled), Date.from(eventTime));
        }
    }
}
