package com.nincraft.ninbot.components.trivia;

import lombok.val;
import net.dv8tion.jda.core.JDA;

import java.util.Random;
import java.util.TimerTask;

public class TriviaHintTask extends TimerTask {

    private TriviaInstance triviaInstance;
    private TriviaInstanceDao triviaInstanceDao;
    private JDA jda;
    private int hintNumber;
    private Random random;

    public TriviaHintTask(TriviaInstance triviaInstance, TriviaInstanceDao triviaInstanceDao, JDA jda, int hintNumber) {
        this.triviaInstance = triviaInstance;
        this.triviaInstanceDao = triviaInstanceDao;
        this.jda = jda;
        this.hintNumber = hintNumber;
        random = new Random();
    }

    @Override
    public void run() {
        if (triviaInstanceDao.isActiveTriviaChannel(triviaInstance.getChannelId())) {
            val answer = triviaInstance.getAnswer();
            String hint = "";
            if (hintNumber == 1) {
                hint = replaceAll(answer);
            } else if (hintNumber == 2) {
                hint = replaceSome(answer);
            }
            jda.getTextChannelById(triviaInstance.getChannelId()).sendMessage("Hint! " + hint).queue();
        }
    }

    private String replaceAll(String answer) {
        String newAnswer = "";
        for (char letter : answer.toCharArray()) {
            if (letter == ' ') {
                newAnswer += letter;
            } else {
                newAnswer += "-";
            }
        }
        return newAnswer;
    }

    private String replaceSome(String answer) {
        int count = 0;
        String newAnswer = "";
        for (char letter : answer.toCharArray()) {
            if ((letter == ' ' || random.nextBoolean()) && count < answer.length() / 2) {
                newAnswer += letter;
                count++;
            } else {
                newAnswer += "-";
            }
        }
        return newAnswer;
    }
}
