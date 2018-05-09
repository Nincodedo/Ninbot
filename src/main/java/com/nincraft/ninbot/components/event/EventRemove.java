package com.nincraft.ninbot.components.event;

import java.util.TimerTask;

public class EventRemove extends TimerTask {

    private Event event;
    private EventDao eventDao;

    EventRemove(Event event, EventDao eventDao) {
        this.event = event;
        this.eventDao = eventDao;
    }

    @Override
    public void run() {
        eventDao.removeEvent(event);
    }
}
