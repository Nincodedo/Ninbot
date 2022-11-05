package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.common.message.MessageUtils;
import dev.nincodedo.ninbot.common.persistence.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Entity
@Data
@Accessors(chain = true)
class Poll extends BaseEntity {
    @Transient
    ResourceBundle resourceBundle;
    private String localeString;
    private String title;
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "choice_index")
    private List<String> choices;
    private String result;
    private LocalDateTime endDateTime;
    private String userAvatarUrl;
    private String userName;
    private boolean pollOpen;
    private String serverId;
    private String channelId;
    private String messageId;
    private boolean userChoicesAllowed = false;

    public Poll() {
        pollOpen = true;
    }

    MessageCreateData build() {
        pollOpen = true;
        setupResourceBundle();
        return newPollMessage(resourceBundle.getString("poll.announce.willclose"));
    }

    MessageEditData editOpen() {
        pollOpen = true;
        setupResourceBundle();
        return editPollMessage(resourceBundle.getString("poll.announce.willclose"));
    }

    MessageEditData buildClosed() {
        pollOpen = false;
        setupResourceBundle();
        return editPollMessage(result);
    }

    private void setupResourceBundle() {
        resourceBundle = ResourceBundle.getBundle("lang", Locale.forLanguageTag(localeString));
    }

    private MessageCreateData newPollMessage(String footer) {
        return new MessageCreateBuilder().addEmbeds(buildEmbedMessage(footer)).build();
    }

    private MessageEditData editPollMessage(String footer) {
        return new MessageEditBuilder().setEmbeds(buildEmbedMessage(footer)).build();
    }

    @NotNull
    private MessageEmbed buildEmbedMessage(String footer) {
        return new EmbedBuilder().setTitle(title)
                .setColor(MessageUtils.getColor(userAvatarUrl))
                .setTimestamp(endDateTime.atZone(ZoneId.systemDefault()).toInstant())
                .setAuthor(resourceBundle.getString("poll.announce.authortext") + userName, null, userAvatarUrl)
                .addField(resourceBundle.getString("poll.announce.choices"), buildPollChoices(), false)
                .setFooter(buildFooter(footer), null).build();
    }

    private String buildFooter(String footer) {
        if (userChoicesAllowed && pollOpen) {
            footer = "Reply to this message to add your own poll choice. " + footer;
        }
        return footer;
    }

    private String buildPollChoices() {
        StringBuilder stringBuilder = new StringBuilder();
        char digitalOneEmoji = '1';
        for (String choice : choices) {
            stringBuilder.append(digitalOneEmoji).append("⃣").append(" ").append(choice).append("\n");
            digitalOneEmoji++;
        }
        return stringBuilder.toString();
    }
}
