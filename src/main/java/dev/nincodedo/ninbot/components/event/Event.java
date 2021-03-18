package dev.nincodedo.ninbot.components.event;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Entity
class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String authorName;
    private int subscriptionId;
    private String gameName;
    private String description;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    @Transient
    private String dateFormat = "yyyy-MM-dd";
    @Transient
    private String timeFormat = "hh:mm a";
    @Transient
    private ResourceBundle resourceBundle;
    private String serverId;

    public Event() {
    }

    String buildChannelMessage(String roleId, int minutesBeforeStart) {
        if (minutesBeforeStart > 0) {
            return String.format(resourceBundle.getString("event.announce.message.startinginxminutes"), roleId, name,
                    authorName, minutesBeforeStart);
        } else {
            if (endTime != null) {
                return String.format(resourceBundle.getString("event.announce.message.startingnowandendatx"), roleId,
                        name, authorName, endTime);
            } else {
                return String.format(resourceBundle.getString("event.announce.message.startingnow"), roleId, name,
                        authorName);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(description)) {
            stringBuilder.append("\n");
            stringBuilder.append(description);
        }
        stringBuilder.append(resourceBundle.getString("event.tostring.createdby"));
        stringBuilder.append(authorName);
        stringBuilder.append(resourceBundle.getString("event.tostring.game"));
        stringBuilder.append(StringUtils.capitalize(""));
        stringBuilder.append(resourceBundle.getString("event.tostring.startdate"));
        stringBuilder.append(startTime.format(DateTimeFormatter.ofPattern(dateFormat)));
        stringBuilder.append(resourceBundle.getString("event.tostring.starttime"));
        stringBuilder.append(startTime.format(DateTimeFormatter.ofPattern(timeFormat)));
        if (getEndTime() != null) {
            stringBuilder.append(resourceBundle.getString("event.tostring.enddate"));
            stringBuilder.append(endTime.format(DateTimeFormatter.ofPattern(dateFormat)));
            stringBuilder.append(resourceBundle.getString("event.tostring.endtime"));
            stringBuilder.append(endTime.format(DateTimeFormatter.ofPattern(timeFormat)));
        }
        return stringBuilder.toString();
    }

    public Long getId() {
        return this.id;
    }

    /**
     * @return {@code this}.
     */

    public Event setId(final Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    /**
     * @return {@code this}.
     */

    public Event setName(final String name) {
        this.name = name;
        return this;
    }

    public String getAuthorName() {
        return this.authorName;
    }

    /**
     * @return {@code this}.
     */

    public Event setAuthorName(final String authorName) {
        this.authorName = authorName;
        return this;
    }

    public int getSubscriptionId() {
        return this.subscriptionId;
    }

    /**
     * @return {@code this}.
     */

    public Event setSubscriptionId(final int subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    public String getGameName() {
        return this.gameName;
    }

    /**
     * @return {@code this}.
     */

    public Event setGameName(final String gameName) {
        this.gameName = gameName;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * @return {@code this}.
     */

    public Event setDescription(final String description) {
        this.description = description;
        return this;
    }

    public ZonedDateTime getStartTime() {
        return this.startTime;
    }

    /**
     * @return {@code this}.
     */

    public Event setStartTime(final ZonedDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public ZonedDateTime getEndTime() {
        return this.endTime;
    }

    /**
     * @return {@code this}.
     */

    public Event setEndTime(final ZonedDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getDateFormat() {
        return this.dateFormat;
    }

    /**
     * @return {@code this}.
     */

    public Event setDateFormat(final String dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public String getTimeFormat() {
        return this.timeFormat;
    }

    /**
     * @return {@code this}.
     */

    public Event setTimeFormat(final String timeFormat) {
        this.timeFormat = timeFormat;
        return this;
    }

    public ResourceBundle getResourceBundle() {
        return this.resourceBundle;
    }

    /**
     * @return {@code this}.
     */

    public Event setResourceBundle(final ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        return this;
    }

    public String getServerId() {
        return this.serverId;
    }

    /**
     * @return {@code this}.
     */

    public Event setServerId(final String serverId) {
        this.serverId = serverId;
        return this;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Event)) return false;
        final Event other = (Event) o;
        if (!other.canEqual(this)) return false;
        if (this.getSubscriptionId() != other.getSubscriptionId()) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$authorName = this.getAuthorName();
        final java.lang.Object other$authorName = other.getAuthorName();
        if (this$authorName == null ? other$authorName != null : !this$authorName.equals(other$authorName))
            return false;
        final java.lang.Object this$gameName = this.getGameName();
        final java.lang.Object other$gameName = other.getGameName();
        if (this$gameName == null ? other$gameName != null : !this$gameName.equals(other$gameName)) return false;
        final java.lang.Object this$description = this.getDescription();
        final java.lang.Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        final java.lang.Object this$startTime = this.getStartTime();
        final java.lang.Object other$startTime = other.getStartTime();
        if (this$startTime == null ? other$startTime != null : !this$startTime.equals(other$startTime)) return false;
        final java.lang.Object this$endTime = this.getEndTime();
        final java.lang.Object other$endTime = other.getEndTime();
        if (this$endTime == null ? other$endTime != null : !this$endTime.equals(other$endTime)) return false;
        final java.lang.Object this$dateFormat = this.getDateFormat();
        final java.lang.Object other$dateFormat = other.getDateFormat();
        if (this$dateFormat == null ? other$dateFormat != null : !this$dateFormat.equals(other$dateFormat))
            return false;
        final java.lang.Object this$timeFormat = this.getTimeFormat();
        final java.lang.Object other$timeFormat = other.getTimeFormat();
        if (this$timeFormat == null ? other$timeFormat != null : !this$timeFormat.equals(other$timeFormat))
            return false;
        final java.lang.Object this$resourceBundle = this.getResourceBundle();
        final java.lang.Object other$resourceBundle = other.getResourceBundle();
        if (this$resourceBundle == null ?
                other$resourceBundle != null : !this$resourceBundle.equals(other$resourceBundle)) return false;
        final java.lang.Object this$serverId = this.getServerId();
        final java.lang.Object other$serverId = other.getServerId();
        return this$serverId == null ? other$serverId == null : this$serverId.equals(other$serverId);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Event;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getSubscriptionId();
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $authorName = this.getAuthorName();
        result = result * PRIME + ($authorName == null ? 43 : $authorName.hashCode());
        final java.lang.Object $gameName = this.getGameName();
        result = result * PRIME + ($gameName == null ? 43 : $gameName.hashCode());
        final java.lang.Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final java.lang.Object $startTime = this.getStartTime();
        result = result * PRIME + ($startTime == null ? 43 : $startTime.hashCode());
        final java.lang.Object $endTime = this.getEndTime();
        result = result * PRIME + ($endTime == null ? 43 : $endTime.hashCode());
        final java.lang.Object $dateFormat = this.getDateFormat();
        result = result * PRIME + ($dateFormat == null ? 43 : $dateFormat.hashCode());
        final java.lang.Object $timeFormat = this.getTimeFormat();
        result = result * PRIME + ($timeFormat == null ? 43 : $timeFormat.hashCode());
        final java.lang.Object $resourceBundle = this.getResourceBundle();
        result = result * PRIME + ($resourceBundle == null ? 43 : $resourceBundle.hashCode());
        final java.lang.Object $serverId = this.getServerId();
        result = result * PRIME + ($serverId == null ? 43 : $serverId.hashCode());
        return result;
    }
}
