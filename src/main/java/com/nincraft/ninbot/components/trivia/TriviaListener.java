package com.nincraft.ninbot.components.trivia;

import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TriviaListener extends ListenerAdapter {

    private String channelId;
    private String answer;
    @Getter
    private boolean questionAnswered;

    public TriviaListener(String channelId, String answer) {
        this.channelId = channelId;
        this.answer = answer.trim();
        questionAnswered = false;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot() && event.getChannel().getId().equals(channelId)) {
            val message = event.getMessage().getContentStripped().toLowerCase().trim();
            if (message.equalsIgnoreCase(answer)) {
                event.getChannel().sendMessage(String.format("%s got it right! It was %s.", event.getMember().getEffectiveName(), answer)).queue();
                event.getJDA().removeEventListener(this);
                questionAnswered = true;
            }
        }
    }
}
