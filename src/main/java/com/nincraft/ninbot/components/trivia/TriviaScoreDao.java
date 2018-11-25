package com.nincraft.ninbot.components.trivia;

import com.nincraft.ninbot.components.common.GenericDao;
import lombok.val;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class TriviaScoreDao extends GenericDao<TriviaScore> {

    private static final String USER_ID = "userId";
    private static final String TRIVIA_SCORE_USER_QUERY = "FROM TriviaScore where userId = :userId";

    public TriviaScoreDao(EntityManager entityManager) {
        super(entityManager);
    }

    int addPoints(String userId, int points) {
        int newScore = points;
        val query = entityManager.createQuery(TRIVIA_SCORE_USER_QUERY, TriviaScore.class);
        query.setParameter(USER_ID, userId);
        val results = query.getResultList();
        if (!results.isEmpty()) {
            val triviaScore = results.get(0);
            newScore = triviaScore.getScore() + points;
            triviaScore.setScore(newScore);
            entityManager.persist(triviaScore);
        }
        return newScore;
    }

    int getPlayerScore(String userId) {
        int score = 0;
        val query = entityManager.createQuery(TRIVIA_SCORE_USER_QUERY, TriviaScore.class);
        query.setParameter(USER_ID, userId);
        val results = query.getResultList();
        if (!results.isEmpty()) {
            val triviaScore = results.get(0);
            score = triviaScore.getScore();
        }
        return score;
    }

    void addUser(String userId) {
        TriviaScore triviaScore = new TriviaScore();
        triviaScore.setScore(0);
        triviaScore.setUserId(userId);
        val query = entityManager.createQuery(TRIVIA_SCORE_USER_QUERY, TriviaScore.class);
        query.setParameter(USER_ID, userId);
        val list = query.getResultList();
        if (list.isEmpty()) {
            entityManager.persist(triviaScore);
        }
    }

    List<TriviaScore> getScoreForAllPlayers() {
        val query = entityManager.createQuery("FROM TriviaScore ", TriviaScore.class);
        return query.getResultList();
    }
}
