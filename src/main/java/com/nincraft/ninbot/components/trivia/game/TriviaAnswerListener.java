package com.nincraft.ninbot.components.trivia.game;

import com.nincraft.ninbot.components.trivia.TriviaScoreService;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TriviaAnswerListener extends ListenerAdapter {

    private String channelId;
    private String answer;
    @Getter
    private boolean questionAnswered;
    @Getter
    private boolean someAnswerOfSorts;
    private TriviaScoreService triviaScoreService;

    TriviaAnswerListener(String channelId, String answer,
            TriviaScoreService triviaScoreService) {
        this.channelId = channelId;
        this.answer = answer;
        questionAnswered = false;
        someAnswerOfSorts = false;
        this.triviaScoreService = triviaScoreService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot() && event.getChannel().getId().equals(channelId)) {
            someAnswerOfSorts = true;
            val message = event.getMessage().getContentStripped().toLowerCase().trim();
            if (message.equalsIgnoreCase(answer)) {
                triviaScoreService.addUser(event.getAuthor().getId());
                int newScore = triviaScoreService.addPoints(event.getAuthor().getId(), 1);
                event.getChannel().sendMessage(String.format("%s got it right! It was %s. They now have %s point(s)", event.getMember().getEffectiveName(), answer, newScore)).queue();
                event.getJDA().removeEventListener(this);
                questionAnswered = true;
            }
        }
    }
}
