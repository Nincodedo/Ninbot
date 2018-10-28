package com.nincraft.ninbot.components.trivia;

import lombok.Data;

import javax.persistence.*;
import java.util.Timer;

@Data
@Entity
@Table(name = "TriviaInstance")
public class TriviaInstance {
    @Id
    @GeneratedValue
    @Column(name = "Id", nullable = false)
    private int id;
    @Column(name = "ChannelId", nullable = false)
    private String channelId;
    @Column(name = "ServerID", nullable = false)
    private String serverId;
    @Column(name = "CategoryId")
    private int categoryId;
    @Column(name = "APIToken", nullable = false)
    private String apiToken;
    @Column(name = "Question")
    private String question;
    @Column(name = "Answer")
    private String answer;
    @Transient
    private TriviaQuestion triviaQuestion;
    @Transient
    private Timer triviaTimer;


    TriviaInstance(String serverId, String channelId, int categoryId) {
        this.serverId = serverId;
        this.channelId = channelId;
        this.categoryId = categoryId;
    }

    public TriviaInstance() {
    }
}
