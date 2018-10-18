package com.nincraft.ninbot.components.trivia;

import com.nincraft.ninbot.components.common.GenericDao;
import lombok.val;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TriviaScoreDao extends GenericDao<TriviaScore> {
    public TriviaScoreDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public int addPoints(String userId, int points) {
        try (val session = sessionFactory.openSession()) {
            int newScore = points;
            session.beginTransaction();
            val query = session.createQuery("FROM TriviaScore where userId = :userId", TriviaScore.class);
            query.setParameter("userId", userId);
            val results = query.getResultList();
            if (!results.isEmpty()) {
                val triviaScore = results.get(0);
                newScore = triviaScore.getScore() + points;
                triviaScore.setScore(newScore);
                session.save(triviaScore);
            }
            session.getTransaction().commit();
            return newScore;
        }
    }

    public int getPoints(String userId) {
        try (val session = sessionFactory.openSession()) {
            int score = 0;
            val query = session.createQuery("FROM TriviaScore where userId = :userId", TriviaScore.class);
            query.setParameter("userId", userId);
            val results = query.getResultList();
            if (!results.isEmpty()) {
                val triviaScore = results.get(0);
                score = triviaScore.getScore();
            }
            return score;
        }
    }

    public void addUser(String userId) {
        TriviaScore triviaScore = new TriviaScore();
        triviaScore.setScore(0);
        triviaScore.setUserId(userId);
        try (val session = sessionFactory.openSession()) {
            val query = session.createQuery("FROM TriviaScore where userId = :userId", TriviaScore.class);
            query.setParameter("userId", userId);
            val list = query.getResultList();
            if (list.isEmpty()) {
                session.beginTransaction();
                session.persist(triviaScore);
                session.getTransaction().commit();
            }
        }
    }

    public List<TriviaScore> getPointsForAllPlayers() {
        try (val session = sessionFactory.openSession()) {
            val query = session.createQuery("FROM TriviaScore ", TriviaScore.class);
            return query.getResultList();
        }
    }
}
