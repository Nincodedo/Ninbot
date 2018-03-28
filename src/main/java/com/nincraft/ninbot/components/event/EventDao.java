package com.nincraft.ninbot.components.event;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Log4j2
@Repository
public class EventDao {

    private final EntityManager entityManager;

    @Autowired
    public EventDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    List<Event> getAllEvents() {
        return entityManager.createQuery("FROM Event", Event.class).getResultList();
    }

    void addEvent(Event event) {
        entityManager.persist(event);
    }

    void removeEvent(Event event) {
        entityManager.remove(event);
    }
}
