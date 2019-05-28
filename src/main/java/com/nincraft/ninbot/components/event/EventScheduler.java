package com.nincraft.ninbot.components.event;

import com.nincraft.ninbot.components.common.Schedulable;
import com.nincraft.ninbot.components.common.LocaleService;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.JDA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Timer;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Date.from;

@Log4j2
@Component
public class EventScheduler implements Schedulable {

    private EventRepository eventRepository;
    private ConfigService configService;
    private LocaleService localeService;

    @Autowired
    public EventScheduler(EventRepository eventRepository, ConfigService configService, LocaleService localeService) {
        this.eventRepository = eventRepository;
        this.configService = configService;
        this.localeService = localeService;
    }

    public void scheduleAll(JDA jda) {
        log.trace("scheduling events");
        val eventList = new ArrayList<Event>();
        eventRepository.findAll().forEach(eventList::add);
        eventList.sort(Comparator.comparing(Event::getStartTime));
        eventList.forEach(event -> scheduleOne(event, jda));
    }

    void addEvent(Event event, JDA jda) {
        eventRepository.save(event);
        scheduleOne(event, jda);
    }

    private void scheduleOne(Event event, JDA jda) {
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
            new EventRemove(event, eventRepository).run();
        } else {
            Timer timer = new Timer();
            log.info("Scheduling {} for {}", event.getName(), event.getStartTime());
            scheduleOne(event, timer, eventStartTime, 0, jda);
            scheduleOne(event, timer, eventEarlyReminder, minutesBeforeStart, jda);
            timer.schedule(new EventRemove(event, eventRepository), from(eventEndTime));
        }
    }

    private void scheduleOne(Event event, Timer timer, Instant eventTime, int minutesBeforeStart, JDA jda) {
        if (!eventTime.isBefore(now())) {
            timer.schedule(new EventAnnounce(event, minutesBeforeStart, configService, jda, localeService), from(eventTime));
        }
    }
}
