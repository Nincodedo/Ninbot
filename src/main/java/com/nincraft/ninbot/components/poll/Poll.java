package com.nincraft.ninbot.components.poll;

import lombok.Data;
import lombok.val;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Data
class Poll {
    private String title;
    private List<String> choices;
    private String result;
    private long timeLength;

    Poll(Message message) {
        val pollMessage = message.getContentStripped().substring("@Ninbot poll ".length());
        choices = new ArrayList<>();
        if (pollMessage.contains("\"")) {
            this.title = pollMessage.substring(0, pollMessage.indexOf("\""));
            val pollOptions = pollMessage.substring(
                    pollMessage.indexOf("\"") + 1, pollMessage.lastIndexOf("\"")).replace("\"", "");
            this.choices = Arrays.asList(pollOptions.split(", "));
            val timeString = pollMessage.substring(pollMessage.lastIndexOf("\"") + 1).trim();
            if (StringUtils.isNotBlank(timeString)) {
                this.timeLength = Long.valueOf(timeString);
            } else {
                this.timeLength = 5L;
            }
        }
    }

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
        IntStream.range(0, choices.size()).forEachOrdered(i -> {
            String choice = choices.get(i);
            stringBuilder.append(i + 1).append(": ").append(choice).append("\n");
        });
        return stringBuilder.toString();
    }
}
