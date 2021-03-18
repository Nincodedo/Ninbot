package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.components.common.message.MessageBuilderHelper;
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
class Poll {
    @Transient
    ResourceBundle resourceBundle;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String localeString;
    private String title;
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "choice_index")
    private List<String> choices;
    private String result;
    private long timeLength;
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
        if (userChoicesAllowed && pollOpen) {
            footer = "Reply to this message to add your own poll choice. " + footer;
        }
        embedBuilder.setFooter(footer, null);
        return new MessageBuilder(embedBuilder).build();
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


    public ResourceBundle getResourceBundle() {
        return this.resourceBundle;
    }

    public void setResourceBundle(final ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getLocaleString() {
        return this.localeString;
    }

    public void setLocaleString(final String localeString) {
        this.localeString = localeString;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public List<String> getChoices() {
        return this.choices;
    }

    public void setChoices(final List<String> choices) {
        this.choices = choices;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(final String result) {
        this.result = result;
    }

    public long getTimeLength() {
        return this.timeLength;
    }

    public void setTimeLength(final long timeLength) {
        this.timeLength = timeLength;
    }

    public String getUserAvatarUrl() {
        return this.userAvatarUrl;
    }

    public void setUserAvatarUrl(final String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public boolean isPollOpen() {
        return this.pollOpen;
    }

    public void setPollOpen(final boolean pollOpen) {
        this.pollOpen = pollOpen;
    }

    public String getServerId() {
        return this.serverId;
    }

    public void setServerId(final String serverId) {
        this.serverId = serverId;
    }

    public String getChannelId() {
        return this.channelId;
    }

    public void setChannelId(final String channelId) {
        this.channelId = channelId;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    public boolean isUserChoicesAllowed() {
        return this.userChoicesAllowed;
    }

    public void setUserChoicesAllowed(final boolean userChoicesAllowed) {
        this.userChoicesAllowed = userChoicesAllowed;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Poll)) return false;
        final Poll other = (Poll) o;
        if (!other.canEqual(this)) return false;
        if (this.getTimeLength() != other.getTimeLength()) return false;
        if (this.isPollOpen() != other.isPollOpen()) return false;
        if (this.isUserChoicesAllowed() != other.isUserChoicesAllowed()) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$resourceBundle = this.getResourceBundle();
        final java.lang.Object other$resourceBundle = other.getResourceBundle();
        if (this$resourceBundle == null ?
                other$resourceBundle != null : !this$resourceBundle.equals(other$resourceBundle)) return false;
        final java.lang.Object this$localeString = this.getLocaleString();
        final java.lang.Object other$localeString = other.getLocaleString();
        if (this$localeString == null ? other$localeString != null : !this$localeString.equals(other$localeString))
            return false;
        final java.lang.Object this$title = this.getTitle();
        final java.lang.Object other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
        final java.lang.Object this$choices = this.getChoices();
        final java.lang.Object other$choices = other.getChoices();
        if (this$choices == null ? other$choices != null : !this$choices.equals(other$choices)) return false;
        final java.lang.Object this$result = this.getResult();
        final java.lang.Object other$result = other.getResult();
        if (this$result == null ? other$result != null : !this$result.equals(other$result)) return false;
        final java.lang.Object this$userAvatarUrl = this.getUserAvatarUrl();
        final java.lang.Object other$userAvatarUrl = other.getUserAvatarUrl();
        if (this$userAvatarUrl == null ? other$userAvatarUrl != null : !this$userAvatarUrl.equals(other$userAvatarUrl))
            return false;
        final java.lang.Object this$userName = this.getUserName();
        final java.lang.Object other$userName = other.getUserName();
        if (this$userName == null ? other$userName != null : !this$userName.equals(other$userName)) return false;
        final java.lang.Object this$serverId = this.getServerId();
        final java.lang.Object other$serverId = other.getServerId();
        if (this$serverId == null ? other$serverId != null : !this$serverId.equals(other$serverId)) return false;
        final java.lang.Object this$channelId = this.getChannelId();
        final java.lang.Object other$channelId = other.getChannelId();
        if (this$channelId == null ? other$channelId != null : !this$channelId.equals(other$channelId)) return false;
        final java.lang.Object this$messageId = this.getMessageId();
        final java.lang.Object other$messageId = other.getMessageId();
        return this$messageId == null ? other$messageId == null : this$messageId.equals(other$messageId);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Poll;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $timeLength = this.getTimeLength();
        result = result * PRIME + (int) ($timeLength >>> 32 ^ $timeLength);
        result = result * PRIME + (this.isPollOpen() ? 79 : 97);
        result = result * PRIME + (this.isUserChoicesAllowed() ? 79 : 97);
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $resourceBundle = this.getResourceBundle();
        result = result * PRIME + ($resourceBundle == null ? 43 : $resourceBundle.hashCode());
        final java.lang.Object $localeString = this.getLocaleString();
        result = result * PRIME + ($localeString == null ? 43 : $localeString.hashCode());
        final java.lang.Object $title = this.getTitle();
        result = result * PRIME + ($title == null ? 43 : $title.hashCode());
        final java.lang.Object $choices = this.getChoices();
        result = result * PRIME + ($choices == null ? 43 : $choices.hashCode());
        final java.lang.Object $result = this.getResult();
        result = result * PRIME + ($result == null ? 43 : $result.hashCode());
        final java.lang.Object $userAvatarUrl = this.getUserAvatarUrl();
        result = result * PRIME + ($userAvatarUrl == null ? 43 : $userAvatarUrl.hashCode());
        final java.lang.Object $userName = this.getUserName();
        result = result * PRIME + ($userName == null ? 43 : $userName.hashCode());
        final java.lang.Object $serverId = this.getServerId();
        result = result * PRIME + ($serverId == null ? 43 : $serverId.hashCode());
        final java.lang.Object $channelId = this.getChannelId();
        result = result * PRIME + ($channelId == null ? 43 : $channelId.hashCode());
        final java.lang.Object $messageId = this.getMessageId();
        result = result * PRIME + ($messageId == null ? 43 : $messageId.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "Poll(resourceBundle=" + this.getResourceBundle() + ", id=" + this.getId() + ", localeString="
                + this.getLocaleString() + ", title=" + this.getTitle() + ", choices=" + this.getChoices() + ", result="
                + this.getResult() + ", timeLength=" + this.getTimeLength() + ", userAvatarUrl="
                + this.getUserAvatarUrl() + ", userName=" + this.getUserName() + ", pollOpen=" + this.isPollOpen()
                + ", serverId=" + this.getServerId() + ", channelId=" + this.getChannelId() + ", messageId="
                + this.getMessageId() + ", userChoicesAllowed=" + this.isUserChoicesAllowed() + ")";
    }
}
