package com.nincraft.ninbot.container;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Data
public class Event {
    private String name;
    private String authorName;
    private String gameName;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public String buildChannelMessage() {
        if (StringUtils.isNotBlank(endTime.toString())) {
            return String.format("Event %s created by %s is starting at %s and will end at %s, %s", name, authorName,
                    startTime, endTime, gameName);
        } else {
            return String.format("Event %s created by %s is starting at %s, %s", name, authorName, startTime, gameName);
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
