package com.nincraft.ninbot.components.poll;

import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

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
    private Member member;

    Message build() {
        return buildPollMessage(resourceBundle.getString("poll.announce.willclose"), Instant.now()
                .plus(timeLength, ChronoUnit.MINUTES));
    }

    Message buildClosed() {
        return buildPollMessage(result, Instant.now());
    }

    private Message buildPollMessage(String footer, Instant timestamp) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setColor(MessageBuilderHelper.getColor(member.getUser().getAvatarUrl()));
        embedBuilder.setTimestamp(timestamp);
        embedBuilder.setAuthor(
                resourceBundle.getString("poll.announce.authortext") + member.getEffectiveName(),
                null, member.getUser().getAvatarUrl());
        embedBuilder.addField(resourceBundle.getString("poll.announce.choices"), buildPollChoices(), false);
        embedBuilder.setFooter(footer, null);
        return new MessageBuilder(embedBuilder).build();
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
