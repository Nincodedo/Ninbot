package com.nincraft.ninbot.components.leaderboard;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ResourceBundle;

@Data
@Entity
public class LeaderboardEntry implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String serverId;
    @Column(nullable = false)
    private String userId;
    private int wins = 0;
    private int ties = 0;
    private int loses = 0;
    @Transient
    private ResourceBundle resourceBundle;

    public LeaderboardEntry() {
        //no-op
    }

    LeaderboardEntry(String serverId, String userId) {
        this.serverId = serverId;
        this.userId = userId;
    }

    String getRecord() {
        if (ties == 0) {
            return String.format(resourceBundle.getString("command.leaderboard.display.entry.noties"), wins, loses);
        } else {
            return String.format(resourceBundle.getString("command.leaderboard.display.entry.ties"), wins, loses, ties);
        }
    }
}
