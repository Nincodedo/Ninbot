package com.nincraft.ninbot.components.event;

import java.util.List;


public interface IEventDao {
    void addEvent(Event event);

    void removeEvent(Event event);

    List<Event> getAllEvents();
}
