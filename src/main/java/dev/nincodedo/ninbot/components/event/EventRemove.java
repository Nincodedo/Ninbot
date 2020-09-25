package dev.nincodedo.ninbot.components.event;

import java.util.TimerTask;

class EventRemove extends TimerTask {

    private Event event;
    private EventRepository eventRepository;

    EventRemove(Event event, EventRepository eventRepository) {
        this.event = event;
        this.eventRepository = eventRepository;
    }

    @Override
    public void run() {
        eventRepository.delete(event);
    }
}
