package com.nincraft.ninbot.components.trivia;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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
    public int getPlayerScore(String userId) {
        return triviaScoreDao.getPlayerScore(userId);
    }

    @Transactional
    public List<TriviaScore> getScoreForAllPlayers() {
        return triviaScoreDao.getScoreForAllPlayers();
    }
}
