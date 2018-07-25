package com.nincraft.ninbot.components.countdown;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Countdown")
@Accessors(chain = true)
class Countdown {
    @Id
    @GeneratedValue
    @Column(name = "Id", nullable = false)
    private int id;
    @Column(name = "Name", nullable = false)
    private String name;
    @Column(name = "EventDate", nullable = false)
    private LocalDate eventDate;
    @Column(name = "ServerId", nullable = false)
    private String serverId;
    @Column(name = "ChannelId")
    private String channelId;

    String buildMessage(long dayDifference) {
        if (dayDifference == 1) {
            return String.format("Countdown event %s is tomorrow!", name);
        } else if (dayDifference == 0) {
            return String.format("Countdown event %s is today!", name);
        } else {
            return String.format("Countdown event %s is in %d days.", name, dayDifference);
        }
    }
}
