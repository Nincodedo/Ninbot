package com.nincraft.ninbot.components.poll;

import lombok.Data;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
class Poll {
    private String title;
    private List<String> choices;
    private String result;
    private long timeLength;

    Message build() {
        return buildPollMessage("Poll will close in " + timeLength + " minutes at "
                + Instant.now().plus(timeLength, ChronoUnit.MINUTES));
    }

    Message buildClosed() {
        return buildPollMessage(result);
    }

    private Message buildPollMessage(String footer) {
        MessageBuilder messageBuilder = new MessageBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
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
