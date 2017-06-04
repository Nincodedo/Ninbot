package com.nincraft.ninbot.container;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Data
public class Event {
    private int id;
    private String name;
    private String authorName;
    private String gameName;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int hidden;

    public String buildChannelMessage(String roleId, int minutesBeforeStart) {
        if (minutesBeforeStart > 0) {
            return String.format("<@&%s>, event %s created by %s is starting in %d minutes", roleId, name, authorName, minutesBeforeStart);
        } else {
            if (StringUtils.isNotBlank(endTime.toString())) {
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
        stringBuilder.append("Name: ");
        stringBuilder.append(name);
        if (StringUtils.isNotBlank(description)) {
            stringBuilder.append("\n");
            stringBuilder.append(description);

        }
        stringBuilder.append("\nCreated by: ");
        stringBuilder.append(authorName);
        stringBuilder.append("\nGame: ");
        stringBuilder.append(StringUtils.capitalize(gameName));
        stringBuilder.append("\nStart Time: ");
        stringBuilder.append(startTime);
        if (StringUtils.isNotBlank(endTime.toString())) {
            stringBuilder.append("\nEnd Time: ");
            stringBuilder.append(endTime);
        }
        return stringBuilder.toString();
    }
}
