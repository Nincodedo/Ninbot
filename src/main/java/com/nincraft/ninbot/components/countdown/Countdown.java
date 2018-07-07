package com.nincraft.ninbot.components.countdown;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Countdown")
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
}
