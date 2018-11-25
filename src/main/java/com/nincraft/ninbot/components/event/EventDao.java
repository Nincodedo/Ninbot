package com.nincraft.ninbot.components.event;

import com.nincraft.ninbot.components.common.GenericDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Repository
@Transactional
public class EventDao extends GenericDao<Event> {

    public EventDao(EntityManager entityManager) {
        super(entityManager);
    }
}
