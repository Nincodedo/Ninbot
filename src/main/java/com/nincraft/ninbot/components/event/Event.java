package com.nincraft.ninbot.components.event;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Accessors(chain = true)
@Entity
class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
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
