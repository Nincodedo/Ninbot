package com.nincraft.ninbot.components.trivia;

import com.nincraft.ninbot.components.common.GenericDao;
import lombok.val;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class TriviaInstanceDao extends GenericDao<TriviaInstance> {
    public TriviaInstanceDao(EntityManager entityManager) {
        super(entityManager);
    }

    public boolean isActiveTriviaChannel(String channelId) {
        val query = entityManager.createQuery("FROM TriviaInstance where channelId = :channelId", TriviaInstance.class);
        query.setParameter("channelId", channelId);
        return !query.getResultList().isEmpty();
    }

    public void removeTriviaInChannel(String channelId) {
        val query = entityManager.createQuery("FROM TriviaInstance where channelId = :channelId", TriviaInstance.class);
        query.setParameter("channelId", channelId);
        for (val trivia : query.getResultList()) {
            entityManager.remove(trivia);
        }
    }
}
