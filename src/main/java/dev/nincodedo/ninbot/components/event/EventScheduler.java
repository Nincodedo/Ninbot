package dev.nincodedo.ninbot.components.event;

import dev.nincodedo.ninbot.components.common.Schedulable;
import dev.nincodedo.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;
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

    @Autowired
    public EventScheduler(EventRepository eventRepository, ConfigService configService) {
        this.eventRepository = eventRepository;
        this.configService = configService;
    }

    public void scheduleAll(ShardManager shardManager) {
        log.trace("scheduling events");
        var eventList = new ArrayList<Event>();
        eventRepository.findAll().forEach(eventList::add);
        eventList.sort(Comparator.comparing(Event::getStartTime));
        eventList.forEach(event -> scheduleOne(event, shardManager));
    }

    void addEvent(Event event, ShardManager shardManager) {
        eventRepository.save(event);
        scheduleOne(event, shardManager);
    }

    private void scheduleOne(Event event, ShardManager shardManager) {
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
            log.debug("Removing event {}, the end time is passed", event.getId());
            new EventRemove(event, eventRepository).run();
        } else {
            Timer timer = new Timer();
            log.trace("Scheduling {} for {}", event.getId(), event.getStartTime());
            var guild = shardManager.getGuildById(event.getServerId());
            scheduleOne(event, timer, eventStartTime, 0, guild);
            scheduleOne(event, timer, eventEarlyReminder, minutesBeforeStart, guild);
            timer.schedule(new EventRemove(event, eventRepository), from(eventEndTime));
        }
    }

    private void scheduleOne(Event event, Timer timer, Instant eventTime, int minutesBeforeStart, Guild guild) {
        if (!eventTime.isBefore(now())) {
            timer.schedule(new EventAnnounce(event, minutesBeforeStart, configService, guild),
                    from(eventTime));
        }
    }
}
