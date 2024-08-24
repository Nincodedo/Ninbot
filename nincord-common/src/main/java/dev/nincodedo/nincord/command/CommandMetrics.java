package dev.nincodedo.nincord.command;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Component;

@Component
public class CommandMetrics {
    private Counter eventsOutCounter = Metrics.counter("bot.listener.commands.events.out");
    private Counter eventsErrorCounter = Metrics.counter("bot.listener.commands.events.error");

    public void incrementEventsOut() {
        eventsOutCounter.increment();
    }

    public void incrementEventsError() {
        eventsErrorCounter.increment();
    }
}
