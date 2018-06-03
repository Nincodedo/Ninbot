package com.nincraft.ninbot.components.event;

import com.nincraft.ninbot.components.common.GenericDao;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class EventDao extends GenericDao<Event> {

    public EventDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
