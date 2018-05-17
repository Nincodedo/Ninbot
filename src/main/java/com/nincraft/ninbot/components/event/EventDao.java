package com.nincraft.ninbot.components.event;

import lombok.val;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public class EventDao {

    private SessionFactory sessionFactory;

    @Autowired
    public EventDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    List<Event> getAllEvents() {
        try (val session = sessionFactory.openSession()) {
            return session.createQuery("FROM Event", Event.class).getResultList();
        }
    }

    void addEvent(Event event) {
        try (val session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(event);
            transaction.commit();
        }
    }

    void removeEvent(Event event) {
        try (val session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(session.contains(event) ? event : session.merge(event));
            transaction.commit();
        }
    }
}
