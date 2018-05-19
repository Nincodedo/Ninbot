package com.nincraft.ninbot.components.event;

import lombok.val;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventDao {

    private SessionFactory sessionFactory;

    @Autowired
    public EventDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    List<Event> getAllEvents() {
        try (val session = sessionFactory.openSession()) {
            return session.createQuery("FROM Event", Event.class).getResultList();
        }
    }

    void addEvent(Event event) {
        try (val session = sessionFactory.openSession()) {
            session.persist(event);
        }
    }

    void removeEvent(Event event) {
        try (val session = sessionFactory.openSession()) {
            session.delete(session.contains(event) ? event : session.merge(event));
        }
    }
}
