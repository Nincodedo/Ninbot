package com.nincraft.ninbot.components.event;

import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.JDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Timer;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Date.from;

@Log4j2
@Component
public class EventScheduler {

    private EventService eventService;

    @Value("${debugEnabled:false}")
    private boolean isDebugEnabled;

    private ConfigService configService;

    @Autowired
    public EventScheduler(EventService eventService, ConfigService configService) {
        this.eventService = eventService;
        this.configService = configService;
    }

    public void scheduleAll(JDA jda) {
        log.trace("scheduling events");
        val events = eventService.getAllEvents();
        events.forEach(event -> scheduleEvent(event, jda));
    }

    void addEvent(Event event, JDA jda) {
        eventService.addEvent(event);
        scheduleEvent(event, jda);
    }

    private void scheduleEvent(Event event, JDA jda) {
        Timer timer = new Timer();
        Instant eventStartTime = event.getStartTime().toInstant();
        int minutesBeforeStart = 30;
        Instant eventEarlyReminder = event.getStartTime().toInstant().minus(minutesBeforeStart, MINUTES);
        Instant eventEndTime;
        if (event.getEndTime() != null) {
            eventEndTime = event.getStartTime().toInstant();
        } else {
            eventEndTime = eventStartTime.plus(1, DAYS);
        }
        if (eventEndTime.isBefore(now()) ||
                (event.getEndTime() == null && eventStartTime.plus(1, DAYS).isBefore(now()))) {
            log.debug("Removing event {}, the end time is passed", event.getName());
            new EventRemove(event, eventService).run();
        } else {
            log.info("Scheduling {} for {}", event.getName(), event.getStartTime());
            scheduleEvent(event, timer, eventStartTime, 0, jda);
            scheduleEvent(event, timer, eventEarlyReminder, minutesBeforeStart, jda);
            timer.schedule(new EventRemove(event, eventService), from(eventEndTime));
        }
    }

    private void scheduleEvent(Event event, Timer timer, Instant eventTime, int minutesBeforeStart, JDA jda) {
        if (!eventTime.isBefore(now())) {
            timer.schedule(new EventAnnounce(event, minutesBeforeStart, isDebugEnabled, configService, jda), from(eventTime));
        }
    }
}
