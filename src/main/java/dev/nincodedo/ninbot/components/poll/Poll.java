package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.components.common.MessageBuilderHelper;
import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;

import javax.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Entity
@Data
class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String localeString;
    @Transient
    ResourceBundle resourceBundle;
    private String title;
    @ElementCollection
    private List<String> choices;
    private String result;
    private long timeLength;
    private String userAvatarUrl;
    private String userName;
    private boolean pollOpen;

    public Poll() {
        pollOpen = true;
    }

    Message build() {
        pollOpen = true;
        setupResourceBundle();
        return buildPollMessage(resourceBundle.getString("poll.announce.willclose"));
    }

    Message buildClosed() {
        pollOpen = false;
        setupResourceBundle();
        return buildPollMessage(result);
    }

    private void setupResourceBundle() {
        resourceBundle = ResourceBundle.getBundle("lang", new Locale(localeString));
    }

    private Message buildPollMessage(String footer) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setColor(MessageBuilderHelper.getColor(userAvatarUrl));
        if (isPollOpen()) {
            embedBuilder.setTimestamp(Instant.now().plus(timeLength, ChronoUnit.MINUTES));
        } else {
            embedBuilder.setTimestamp(Instant.now());
        }
        embedBuilder.setAuthor(resourceBundle.getString("poll.announce.authortext") + userName, null, userAvatarUrl);
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
