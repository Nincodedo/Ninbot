package com.nincraft.ninbot.components.trivia;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class TriviaScore implements Comparable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private int score;

    public TriviaScore() {
        //no-op
    }

    TriviaScore(String userId) {
        this.userId = userId;
    }

    @Override
    public int compareTo(Object o) {
        TriviaScore triviaScore = (TriviaScore) o;
        if (triviaScore.getScore() == this.getScore()) {
            return triviaScore.getUserId().compareTo(this.getUserId());
        } else {
            return Integer.compare(triviaScore.getScore(), this.getScore());
        }
    }
}
