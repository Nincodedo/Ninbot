package com.nincraft.ninbot.components.event;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Accessors(chain = true)
class Event {
    private int id;
    private String name;
    private String authorName;
    private String gameName;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int hidden;
    private String dateFormat = "yyyy-MM-dd hh:mm a (HH:mm)";

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
        stringBuilder.append(StringUtils.capitalize(gameName));
        stringBuilder.append("\nStart Time: ");
        stringBuilder.append(startTime.format(DateTimeFormatter.ofPattern(dateFormat)));
        if (getEndTime() != null) {
            stringBuilder.append("\nEnd Time: ");
            stringBuilder.append(endTime.format(DateTimeFormatter.ofPattern(dateFormat)));
        }
        return stringBuilder.toString();
    }
}
