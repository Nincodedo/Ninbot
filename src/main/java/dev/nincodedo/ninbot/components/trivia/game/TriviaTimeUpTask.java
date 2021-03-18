package dev.nincodedo.ninbot.components.trivia.game;

import dev.nincodedo.ninbot.components.trivia.TriviaInstance;
import dev.nincodedo.ninbot.components.trivia.TriviaInstanceRepository;
import net.dv8tion.jda.api.JDA;

import java.util.TimerTask;

public class TriviaTimeUpTask extends TimerTask {
    private TriviaInstance triviaInstance;
    private TriviaInstanceRepository triviaInstanceRepository;
    private JDA jda;
    private boolean timeExpired;

    TriviaTimeUpTask(TriviaInstance triviaInstance, TriviaInstanceRepository triviaInstanceRepository, JDA jda) {
        this.triviaInstance = triviaInstance;
        this.triviaInstanceRepository = triviaInstanceRepository;
        this.jda = jda;
        this.timeExpired = false;
    }

    @Override
    public void run() {
        if (triviaInstanceRepository.existsByChannelId(triviaInstance.getChannelId())) {
            final net.dv8tion.jda.api.entities.TextChannel channel =
                    jda.getTextChannelById(triviaInstance.getChannelId());
            channel.sendMessage(String.format("Time is up! The correct answer was %s", triviaInstance.getAnswer()))
                    .queue();
            timeExpired = true;
        }
    }


    public boolean isTimeExpired() {
        return this.timeExpired;
    }
}
