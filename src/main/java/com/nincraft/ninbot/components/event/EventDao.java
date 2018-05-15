package com.nincraft.ninbot.components.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class EventDao {

    private final EntityManager entityManager;

    @Autowired
    public EventDao(@Qualifier("entityManagerFactoryBean") EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    List<Event> getAllEvents() {
        return entityManager.createQuery("FROM Event", Event.class).getResultList();
    }

    @Transactional
    void addEvent(Event event) {
        entityManager.persist(event);
    }

    @Transactional
    void removeEvent(Event event) {
        entityManager.remove(event);
    }
}
