package dev.nincodedo.ninbot.components.event;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Data
@Accessors(chain = true)
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

    String buildChannelMessage(String roleId, int minutesBeforeStart) {
        if (minutesBeforeStart > 0) {
            return String.format(resourceBundle.getString("event.announce.message.startinginxminutes"), roleId, name,
                    authorName, minutesBeforeStart);
        } else if (endTime != null) {
            return String.format(resourceBundle.getString("event.announce.message.startingnowandendatx"), roleId,
                    name, authorName,
                    endTime);
        } else {
            return String.format(resourceBundle.getString("event.announce.message.startingnow"), roleId, name,
                    authorName);
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
}
