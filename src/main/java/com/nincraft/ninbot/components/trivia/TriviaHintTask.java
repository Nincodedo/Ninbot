package com.nincraft.ninbot.components.trivia;

import lombok.val;
import net.dv8tion.jda.core.JDA;

import java.util.*;

class TriviaHintTask extends TimerTask {

    private TriviaInstance triviaInstance;
    private TriviaInstanceDao triviaInstanceDao;
    private JDA jda;
    private int hintNumber;
    private Random random;

    TriviaHintTask(TriviaInstance triviaInstance, TriviaInstanceDao triviaInstanceDao, JDA jda, int hintNumber) {
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
            String hint = revealHint(answer, hintNumber);
            jda.getTextChannelById(triviaInstance.getChannelId()).sendMessage("Hint! " + hint).queue();
        }
    }

    private String revealHint(String answer, int hintNumber) {
        StringBuilder newAnswer = new StringBuilder();
        double percentOfForcedHint = ((hintNumber - 1) * 40) / 100.0;
        int numOfLettersToShow = (int) (answer.length() * percentOfForcedHint);
        List<Integer> hintIndexes = getHintIndexList(numOfLettersToShow, answer.length());
        char[] charArray = answer.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char letter = charArray[i];
            if (letter == ' ' || hintIndexes.contains(i)) {
                newAnswer.append(letter);
            } else {
                newAnswer.append("-");
            }
        }
        return newAnswer.toString();
    }

    private List<Integer> getHintIndexList(int numOfLettersToShow, int length) {
        Set<Integer> hintIndexes = new HashSet<>();
        for (int i = 0; i < numOfLettersToShow; i++) {
            hintIndexes.add(random.nextInt(length));
        }
        return new ArrayList<>(hintIndexes);
    }
}
