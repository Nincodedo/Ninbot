package com.nincraft.ninbot.components.countdown;

import com.nincraft.ninbot.components.common.GenericDao;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class CountdownDao extends GenericDao<Countdown> {
    public CountdownDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
