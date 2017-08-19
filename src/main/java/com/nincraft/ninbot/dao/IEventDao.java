package com.nincraft.ninbot.dao;

import com.nincraft.ninbot.entity.Event;

import java.util.List;


public interface IEventDao {
    void addEvent(Event event);

    void removeEvent(Event event);

    List<Event> getAllEvents();

    Event getEventByName(String name);

    Event getEventByAuthorName(String authorName);
}
