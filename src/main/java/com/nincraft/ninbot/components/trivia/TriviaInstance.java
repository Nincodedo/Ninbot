package com.nincraft.ninbot.components.trivia;

import com.nincraft.ninbot.components.trivia.game.TriviaQuestion;
import lombok.Data;

import javax.persistence.*;
import java.util.Timer;

@Data
@Entity
public class TriviaInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String channelId;
    @Column(nullable = false)
    private String serverId;
    private int categoryId;
    @Column(nullable = false)
    private String apiToken;
    private String question;
    private String answer;
    @Transient
    private TriviaQuestion triviaQuestion;
    @Transient
    private Timer triviaTimer;


    public TriviaInstance(String serverId, String channelId, int categoryId) {
        this.serverId = serverId;
        this.channelId = channelId;
        this.categoryId = categoryId;
    }

    public TriviaInstance() {
    }
}
