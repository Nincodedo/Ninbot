package com.nincraft.ninbot.components.event;

import com.nincraft.ninbot.components.config.ConfigDao;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.JDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Timer;

@Log4j2
@Component
public class EventScheduler {

    private EventDao eventDao;

    @Value("${debugEnabled:false}")
    private boolean isDebugEnabled;

    private ConfigDao configDao;

    @Autowired
    public EventScheduler(EventDao eventDao, ConfigDao configDao) {
        this.eventDao = eventDao;
        this.configDao = configDao;
    }

    public void scheduleAll(JDA jda) {
        log.trace("scheduling events");
        val events = eventDao.getAllEvents();
        for (val event : events) {
            scheduleEvent(event, jda);
        }
    }

    void addEvent(Event event, JDA jda) {
        eventDao.addEvent(event);
        scheduleEvent(event, jda);
    }

    private void scheduleEvent(Event event, JDA jda) {
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
            scheduleEvent(event, timer, eventStartTime, 0, jda);
            scheduleEvent(event, timer, eventEarlyReminder, minutesBeforeStart, jda);
            timer.schedule(new EventRemove(event, eventDao), Date.from(eventEndTime));
        }
    }

    private void scheduleEvent(Event event, Timer timer, Instant eventTime, int minutesBeforeStart, JDA jda) {
        if (!eventTime.isBefore(Instant.now())) {
            timer.schedule(new EventAnnounce(event, minutesBeforeStart, isDebugEnabled, configDao, jda), Date.from(eventTime));
        }
    }
}
