package com.nincraft.ninbot.components.trivia;

import com.nincraft.ninbot.components.common.GenericDao;
import lombok.val;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class TriviaInstanceDao extends GenericDao<TriviaInstance> {
    public TriviaInstanceDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public boolean isActiveTriviaChannel(String channelId) {
        try (val session = sessionFactory.openSession()) {
            val query = session.createQuery("FROM TriviaInstance where channelId = :channelId", TriviaInstance.class);
            query.setParameter("channelId", channelId);
            return !query.getResultList().isEmpty();
        }
    }

    public void removeTriviaInChannel(String channelId) {
        try (val session = sessionFactory.openSession()) {
            session.beginTransaction();
            val query = session.createQuery("FROM TriviaInstance where channelId = :channelId", TriviaInstance.class);
            query.setParameter("channelId", channelId);
            for (val trivia : query.getResultList()) {
                session.delete(trivia);
            }
            session.getTransaction().commit();
        }
    }
}
