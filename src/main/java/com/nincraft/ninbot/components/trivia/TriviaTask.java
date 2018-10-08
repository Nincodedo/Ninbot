package com.nincraft.ninbot.components.trivia;

import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.core.JDA;

import java.util.TimerTask;

public class TriviaTask extends TimerTask {

    private TriviaInstance triviaInstance;
    private TriviaInstanceDao triviaInstanceDao;
    private JDA jda;
    @Getter
    private boolean timeExpired;

    TriviaTask(TriviaInstance triviaInstance, TriviaInstanceDao triviaInstanceDao,
            JDA jda) {
        this.triviaInstance = triviaInstance;
        this.triviaInstanceDao = triviaInstanceDao;
        this.jda = jda;
        this.timeExpired = false;
    }

    @Override
    public void run() {
        if (triviaInstanceDao.isActiveTriviaChannel(triviaInstance.getChannelId())) {
            val channel = jda.getTextChannelById(triviaInstance.getChannelId());
            channel.sendMessage(String.format("Time is up! The correct answer was %s", triviaInstance.getAnswer())).queue();
            timeExpired = true;
        }
    }
}
