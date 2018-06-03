package com.nincraft.ninbot.components.event;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class EventService {

    private EventDao eventDao;

    public EventService(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Transactional
    public List<Event> getAllEvents() {
        return eventDao.getAllObjects();
    }

    @Transactional
    public void addEvent(Event event) {
        eventDao.saveObject(event);
    }

    @Transactional
    public void removeEvent(Event event) {
        eventDao.removeObject(event);
    }
}
