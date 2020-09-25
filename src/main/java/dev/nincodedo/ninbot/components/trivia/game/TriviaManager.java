package dev.nincodedo.ninbot.components.trivia.game;

import dev.nincodedo.ninbot.components.trivia.TriviaInstance;
import dev.nincodedo.ninbot.components.trivia.TriviaInstanceRepository;
import dev.nincodedo.ninbot.components.trivia.TriviaScoreService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Timer;

@Log4j2
@Component
public class TriviaManager {

    private TriviaInstanceRepository triviaInstanceRepository;
    private TriviaScoreService triviaScoreService;
    private int triviaUnansweredLimit = 3;
    private TriviaAPI triviaAPI;

    public TriviaManager(TriviaInstanceRepository triviaInstanceRepository, TriviaScoreService triviaScoreService,
            TriviaAPI triviaAPI) {
        this.triviaInstanceRepository = triviaInstanceRepository;
        this.triviaScoreService = triviaScoreService;
        this.triviaAPI = triviaAPI;
    }

    private void stopTrivia(TriviaInstance triviaInstance, JDA jda) {
        triviaInstance.getTriviaTimer().cancel();
        jda.getTextChannelById(triviaInstance.getChannelId()).sendMessage(
                "Trivia has ended. No answers from the last " + triviaUnansweredLimit + " questions").queue();
        triviaInstanceRepository.deleteByChannelId(triviaInstance.getChannelId());
    }

    public void stopTrivia(String channelId) {
        triviaInstanceRepository.deleteByChannelId(channelId);
    }

    public boolean isTriviaActiveInChannel(String channelId) {
        return triviaInstanceRepository.existsByChannelId(channelId);
    }

    public void startTrivia(String channelId, int categoryId, String serverId, JDA jda) {
        TriviaInstance triviaInstance = new TriviaInstance(serverId, channelId, categoryId);
        triviaInstance.setTriviaQuestion(triviaAPI.nextTriviaQuestion(triviaInstance));
        triviaInstanceRepository.save(triviaInstance);
        startTriviaLoop(triviaInstance, jda);
    }

    private void startTriviaLoop(TriviaInstance triviaInstance, JDA jda) {
        Thread thread = new Thread(() -> {
            int unansweredQuestionCount = 0;
            while (isTriviaActiveInChannel(triviaInstance.getChannelId())
                    && unansweredQuestionCount <= triviaUnansweredLimit) {
                triviaInstanceRepository.save(triviaInstance);
                if (triviaInstance.getTriviaQuestion() != null) {
                    unansweredQuestionCount = askTriviaQuestion(triviaInstance, jda, unansweredQuestionCount);
                } else {
                    triviaInstance.setTriviaQuestion(triviaAPI.nextTriviaQuestion(triviaInstance));
                }
            }
            if (unansweredQuestionCount > triviaUnansweredLimit) {
                stopTrivia(triviaInstance, jda);
            }
        });
        thread.start();
    }

    private int askTriviaQuestion(TriviaInstance triviaInstance, JDA jda, int unansweredQuestionCount) {
        Timer timer = new Timer();
        triviaInstance.setTriviaTimer(timer);

        val triviaQuestion = triviaInstance.getTriviaQuestion();
        triviaInstance.setAnswer(triviaQuestion.getCorrectAnswer().trim());
        val triviaAnswerListener = new TriviaAnswerListener(triviaInstance.getChannelId(), triviaInstance.getAnswer()
                , triviaScoreService);
        jda.addEventListener(triviaAnswerListener);
        val channel = jda.getTextChannelById(triviaInstance.getChannelId());
        channel.sendMessage(triviaQuestion.build()).queue();
        val triviaTask = new TriviaTimeUpTask(triviaInstance, triviaInstanceRepository, jda);
        long time = 30000L;
        timer.schedule(new TriviaHintTask(triviaInstance, triviaInstanceRepository, jda, 1), 0);
        timer.schedule(new TriviaHintTask(triviaInstance, triviaInstanceRepository, jda, 2), time);
        timer.schedule(new TriviaHintTask(triviaInstance, triviaInstanceRepository, jda, 3), time * 2);
        timer.schedule(triviaTask, time * 3);
        while (!triviaAnswerListener.isQuestionAnswered() && !triviaTask.isTimeExpired()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.error("oh no", e);
            }
        }
        if (triviaAnswerListener.isQuestionAnswered()) {
            unansweredQuestionCount = 0;
            timer.cancel();
        } else if (triviaAnswerListener.isSomeAnswerOfSorts()) {
            unansweredQuestionCount = 0;
        } else {
            unansweredQuestionCount++;
        }
        jda.removeEventListener(triviaAnswerListener);
        triviaInstance.setTriviaQuestion(null);
        return unansweredQuestionCount;
    }

    public Map<Integer, String> getTriviaCategories() {
        return triviaAPI.getTriviaCategories();
    }
}
