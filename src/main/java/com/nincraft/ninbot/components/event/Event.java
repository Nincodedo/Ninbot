package com.nincraft.ninbot.components.event;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "GameEvents")
class Event {
    @Id
    @GeneratedValue
    @Column(name = "Id")
    private int id;
    @Column(name = "Name")
    private String name;
    @Column(name = "AuthorName")
    private String authorName;
    @Column(name = "SubscriptionId")
    private int subscriptionId;
    @Column(name = "GameName")
    private String gameName;
    @Column(name = "Description")
    private String description;
    @Column(name = "StartTime")
    private OffsetDateTime startTime;
    @Column(name = "EndTime")
    private OffsetDateTime endTime;
    @Transient
    private String dateFormat = "yyyy-MM-dd";
    @Transient
    private String timeFormat = "hh:mm a";
    @Column(name = "ServerId")
    private String serverId;

    String buildChannelMessage(String roleId, int minutesBeforeStart) {
        if (minutesBeforeStart > 0) {
            return String.format("<@&%s>, event %s created by %s is starting in %d minutes", roleId, name, authorName, minutesBeforeStart);
        } else {
            if (endTime != null) {
                return String.format("<@&%s>, event %s created by %s is starting now and will end at %s", roleId, name, authorName,
                        endTime);
            } else {
                return String.format("<@&%s>, event %s created by %s is starting now", roleId, name, authorName);
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
        stringBuilder.append("\nCreated by: ");
        stringBuilder.append(authorName);
        stringBuilder.append("\nGame: ");
        stringBuilder.append(StringUtils.capitalize(""));
        stringBuilder.append("\nStart Date: ");
        stringBuilder.append(startTime.format(DateTimeFormatter.ofPattern(dateFormat)));
        stringBuilder.append("\nStart Time: ");
        stringBuilder.append(startTime.format(DateTimeFormatter.ofPattern(timeFormat)));
        if (getEndTime() != null) {
            stringBuilder.append("\nEnd Date: ");
            stringBuilder.append(endTime.format(DateTimeFormatter.ofPattern(dateFormat)));
            stringBuilder.append("\nEnd Time: ");
            stringBuilder.append(endTime.format(DateTimeFormatter.ofPattern(timeFormat)));
        }
        return stringBuilder.toString();
    }
}
