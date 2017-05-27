package com.nincraft.ninbot.action;

import com.nincraft.ninbot.Ninbot;
import com.nincraft.ninbot.container.Event;
import com.nincraft.ninbot.dao.IEventDao;

import java.util.TimerTask;

public class EventRemove extends TimerTask {

    private Event event;
    private IEventDao eventDao;

    public EventRemove(Event event) {
        this.event = event;
        this.eventDao = Ninbot.getEventDao();
    }

    @Override
    public void run() {
        eventDao.removeEvent(event);
    }
}
