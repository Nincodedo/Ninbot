package com.nincraft.ninbot.components.event;

import java.util.TimerTask;

public class EventRemove extends TimerTask {

    private Event event;
    private IEventDao eventDao;

    public EventRemove(Event event, IEventDao eventDao) {
        this.event = event;
        this.eventDao = eventDao;
    }

    @Override
    public void run() {
        eventDao.removeEvent(event);
    }
}
