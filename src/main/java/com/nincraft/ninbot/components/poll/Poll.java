package com.nincraft.ninbot.components.poll;

import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import lombok.Data;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

@Data
class Poll {
    ResourceBundle resourceBundle;
    private String title;
    private List<String> choices;
    private String result;
    private long timeLength;
    private User user;
    private boolean pollOpen;

    Message build() {
        pollOpen = true;
        return buildPollMessage(resourceBundle.getString("poll.announce.willclose"));
    }

    Message buildClosed() {
        pollOpen = false;
        return buildPollMessage(result);
    }

    private Message buildPollMessage(String footer) {
        MessageBuilderHelper messageBuilder = new MessageBuilderHelper();
        messageBuilder.setTitle(title);
        messageBuilder.setColor(user.getAvatarUrl());
        if (isPollOpen()) {
            messageBuilder.setTimestamp(Instant.now().plus(timeLength, ChronoUnit.MINUTES));
        } else {
            messageBuilder.setTimestamp(Instant.now());
        }
        messageBuilder.setAuthor(
                resourceBundle.getString("poll.announce.authortext") + user.getName(), null, user.getAvatarUrl());
        messageBuilder.addField(resourceBundle.getString("poll.announce.choices"), buildPollChoices(), false);
        messageBuilder.setFooter(footer, null);
        return messageBuilder.build();
    }

    private String buildPollChoices() {
        StringBuilder stringBuilder = new StringBuilder();
        char digitalOneEmoji = '\u0031';
        for (String choice : choices) {
            stringBuilder.append(digitalOneEmoji).append("\u20E3").append(" ").append(choice).append("\n");
            digitalOneEmoji++;
        }
        return stringBuilder.toString();
    }
}
