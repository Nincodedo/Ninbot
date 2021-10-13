package dev.nincodedo.ninbot.components.countdown;

import lombok.Data;
import lombok.experimental.Accessors;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

@Data
@Entity
@Accessors(chain = true)
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
    @CreatedBy
    private String creatorId;
    private String channelId;
    @CreatedDate
    private LocalDateTime createdAt;
    @Transient
    private ResourceBundle resourceBundle;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }

    String buildMessage() {
        var dayDifference = getDayDifference();
        if (dayDifference == 1) {
            return String.format(resourceBundle.getString("countdown.announce.message.tomorrow"), name);
        } else if (dayDifference == 0) {
            return String.format(resourceBundle.getString("countdown.announce.message.today"), name);
        } else {
            return String.format(resourceBundle.getString("countdown.announce.message.later"), name, dayDifference);
        }
    }

    public String getEventDateDescription() {
        return TimeFormat.RELATIVE.format(getEventDate().toEpochSecond() * 1000);
    }

    public String getDescription() {
        return resourceBundle.getString("command.countdown.list.starttime") + getEventDateDescription();
    }

    long getDayDifference() {
        var tomorrowDate = LocalDate.now(eventDate.getZone())
                .plus(1, ChronoUnit.DAYS)
                .atStartOfDay()
                .atZone(eventDate.getZone());
        return ChronoUnit.DAYS.between(tomorrowDate, eventDate);
    }
}
