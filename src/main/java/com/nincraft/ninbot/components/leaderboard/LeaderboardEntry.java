package com.nincraft.ninbot.components.leaderboard;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class LeaderboardEntry implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;
    @Column(nullable = false)
    private String serverId;
    @Column(nullable = false)
    private String userId;
    private int wins = 0;
    private int ties = 0;
    private int loses = 0;

    public LeaderboardEntry() {
        //no-op
    }

    LeaderboardEntry(String serverId, String userId) {
        this.serverId = serverId;
        this.userId = userId;
    }

    String getRecord() {
        if (ties == 0) {
            return String.format("Wins: %d, Loses: %d", wins, loses);
        } else {
            return String.format("Wins: %d, Loses: %d, Ties: %d", wins, loses, ties);
        }
    }
}
