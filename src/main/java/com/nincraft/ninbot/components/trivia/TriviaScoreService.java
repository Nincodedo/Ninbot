package com.nincraft.ninbot.components.trivia;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TriviaScoreService {

    private TriviaScoreDao triviaScoreDao;

    public TriviaScoreService(TriviaScoreDao triviaScoreDao) {
        this.triviaScoreDao = triviaScoreDao;
    }

    @Transactional
    public void addUser(String userId) {
        triviaScoreDao.addUser(userId);
    }

    @Transactional
    public int addPoints(String userId, int points) {
        return triviaScoreDao.addPoints(userId, points);
    }

    @Transactional
    public int getPoints(String userId) {
        return triviaScoreDao.getPoints(userId);
    }
}
