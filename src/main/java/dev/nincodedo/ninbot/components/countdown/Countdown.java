package dev.nincodedo.ninbot.components.countdown;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

@Entity
class Countdown {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private ZonedDateTime eventDate;
    @Column(nullable = false)
    private String serverId;
    private String channelId;
    @Transient
    private ResourceBundle resourceBundle;

    public Countdown() {
    }

    String buildMessage() {
        final long dayDifference = getDayDifference();
        if (dayDifference == 1) {
            return String.format(resourceBundle.getString("countdown.announce.message.tomorrow"), name);
        } else if (dayDifference == 0) {
            return String.format(resourceBundle.getString("countdown.announce.message.today"), name);
        } else {
            return String.format(resourceBundle.getString("countdown.announce.message.later"), name, dayDifference);
        }
    }

    public String getDescription() {
        return resourceBundle.getString("command.countdown.list.starttime")
                + getEventDate().format(DateTimeFormatter.ISO_OFFSET_DATE) + "\n"
                + resourceBundle.getString("command.countdown.list.daysuntil") + getDayDifference();
    }

    long getDayDifference() {
        final java.time.ZonedDateTime tomorrowDate = LocalDate.now(eventDate.getZone())
                .plus(1, ChronoUnit.DAYS)
                .atStartOfDay()
                .atZone(eventDate.getZone());
        return ChronoUnit.DAYS.between(tomorrowDate, eventDate);
    }

    public Long getId() {
        return this.id;
    }

    /**
     * @return {@code this}.
     */

    public Countdown setId(final Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    /**
     * @return {@code this}.
     */

    public Countdown setName(final String name) {
        this.name = name;
        return this;
    }

    public ZonedDateTime getEventDate() {
        return this.eventDate;
    }

    /**
     * @return {@code this}.
     */

    public Countdown setEventDate(final ZonedDateTime eventDate) {
        this.eventDate = eventDate;
        return this;
    }

    public String getServerId() {
        return this.serverId;
    }

    /**
     * @return {@code this}.
     */

    public Countdown setServerId(final String serverId) {
        this.serverId = serverId;
        return this;
    }

    public String getChannelId() {
        return this.channelId;
    }

    /**
     * @return {@code this}.
     */

    public Countdown setChannelId(final String channelId) {
        this.channelId = channelId;
        return this;
    }

    public ResourceBundle getResourceBundle() {
        return this.resourceBundle;
    }

    /**
     * @return {@code this}.
     */

    public Countdown setResourceBundle(final ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        return this;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Countdown)) return false;
        final Countdown other = (Countdown) o;
        if (!other.canEqual(this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$eventDate = this.getEventDate();
        final java.lang.Object other$eventDate = other.getEventDate();
        if (this$eventDate == null ? other$eventDate != null : !this$eventDate.equals(other$eventDate)) return false;
        final java.lang.Object this$serverId = this.getServerId();
        final java.lang.Object other$serverId = other.getServerId();
        if (this$serverId == null ? other$serverId != null : !this$serverId.equals(other$serverId)) return false;
        final java.lang.Object this$channelId = this.getChannelId();
        final java.lang.Object other$channelId = other.getChannelId();
        if (this$channelId == null ? other$channelId != null : !this$channelId.equals(other$channelId)) return false;
        final java.lang.Object this$resourceBundle = this.getResourceBundle();
        final java.lang.Object other$resourceBundle = other.getResourceBundle();
        return this$resourceBundle == null ?
                other$resourceBundle == null : this$resourceBundle.equals(other$resourceBundle);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Countdown;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $eventDate = this.getEventDate();
        result = result * PRIME + ($eventDate == null ? 43 : $eventDate.hashCode());
        final java.lang.Object $serverId = this.getServerId();
        result = result * PRIME + ($serverId == null ? 43 : $serverId.hashCode());
        final java.lang.Object $channelId = this.getChannelId();
        result = result * PRIME + ($channelId == null ? 43 : $channelId.hashCode());
        final java.lang.Object $resourceBundle = this.getResourceBundle();
        result = result * PRIME + ($resourceBundle == null ? 43 : $resourceBundle.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "Countdown(id=" + this.getId() + ", name=" + this.getName() + ", eventDate=" + this.getEventDate()
                + ", serverId=" + this.getServerId() + ", channelId=" + this.getChannelId() + ", resourceBundle=" + this
                .getResourceBundle() + ")";
    }
}
