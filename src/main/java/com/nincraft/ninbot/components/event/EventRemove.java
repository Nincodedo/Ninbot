package com.nincraft.ninbot.components.event;

import java.util.TimerTask;

class EventRemove extends TimerTask {

    private Event event;
    private EventService eventService;

    EventRemove(Event event, EventService eventService) {
        this.event = event;
        this.eventService = eventService;
    }

    @Override
    public void run() {
        eventService.removeEvent(event);
    }
}
