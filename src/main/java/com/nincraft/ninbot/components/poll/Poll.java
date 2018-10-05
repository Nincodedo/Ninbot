package com.nincraft.ninbot.components.poll;

import lombok.Data;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
class Poll {
    private String title;
    private List<String> choices;
    private String result;
    private long timeLength;
    private User user;
    private boolean pollOpen;

    Message build() {
        pollOpen = true;
        return buildPollMessage("Poll will close ");
    }

    Message buildClosed() {
        pollOpen = false;
        return buildPollMessage(result);
    }

    private Message buildPollMessage(String footer) {
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        if (isPollOpen()) {
            embedBuilder.setTimestamp(Instant.now().plus(timeLength, ChronoUnit.MINUTES));
        } else {
            embedBuilder.setTimestamp(Instant.now());
        }
        embedBuilder.setAuthor("Poll by " + user.getName(), null, user.getAvatarUrl());
        embedBuilder.addField("Choices", buildPollChoices(), false);
        embedBuilder.setFooter(footer, null);
        messageBuilder.setEmbed(embedBuilder.build());
        return messageBuilder.build();
    }

    private String buildPollChoices() {
        StringBuilder stringBuilder = new StringBuilder();
        char digitalOneEmoji = '\u0031';
        for (String choice : choices) {
            stringBuilder.append(Character.toString(digitalOneEmoji)).append("\u20E3").append(" ").append(choice).append("\n");
            digitalOneEmoji++;
        }
        return stringBuilder.toString();
    }
}
