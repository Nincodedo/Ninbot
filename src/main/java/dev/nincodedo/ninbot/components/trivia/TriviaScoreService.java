package dev.nincodedo.ninbot.components.trivia;

import lombok.val;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class TriviaScoreService {

    private TriviaScoreRepository triviaScoreRepository;

    public TriviaScoreService(TriviaScoreRepository triviaScoreRepository) {
        this.triviaScoreRepository = triviaScoreRepository;
    }

    @Transactional
    public void addUser(String userId) {
        triviaScoreRepository.save(new TriviaScore(userId));
    }

    @Transactional
    public int addPoints(String userId, int points) {
        val triviaScore = triviaScoreRepository.getFirstByUserId(userId);
        if (triviaScore.isPresent()) {
            triviaScore.get().setScore(triviaScore.get().getScore() + points);
            return triviaScore.get().getScore();
        } else {
            return 0;
        }
    }

    @Transactional
    public int getPlayerScore(String userId) {
        val triviaScore = triviaScoreRepository.getFirstByUserId(userId);
        return triviaScore.map(TriviaScore::getScore).orElse(0);
    }

    @Transactional
    public List<TriviaScore> getScoreForAllPlayers() {
        List<TriviaScore> triviaScores = new ArrayList<>();
        triviaScoreRepository.findAll().forEach(triviaScores::add);
        return triviaScores;
    }
}
