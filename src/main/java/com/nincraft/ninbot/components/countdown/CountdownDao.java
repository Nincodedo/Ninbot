package com.nincraft.ninbot.components.countdown;

import com.nincraft.ninbot.components.common.GenericDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class CountdownDao extends GenericDao<Countdown> {
    public CountdownDao(EntityManager entityManager) {
        super(entityManager);
    }
}
