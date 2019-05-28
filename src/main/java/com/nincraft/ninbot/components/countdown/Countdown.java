package com.nincraft.ninbot.components.countdown;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

@Data
@Entity
@Accessors(chain = true)
class Countdown {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private ZonedDateTime eventDate;
    @Column(nullable = false)
    private String serverId;
    private String channelId;
    @Transient
    private ResourceBundle resourceBundle;

    String buildMessage(long dayDifference) {
        if (dayDifference == 1) {
            return String.format(resourceBundle.getString("countdown.announce.message.tomorrow"), name);
        } else if (dayDifference == 0) {
            return String.format(resourceBundle.getString("countdown.announce.message.today"), name);
        } else {
            return String.format(resourceBundle.getString("countdown.announce.message.later"), name, dayDifference);
        }
    }
}
