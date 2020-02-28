package com.nincraft.ninbot.components.trivia;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class TriviaScore implements Comparable<TriviaScore> {
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
    public int compareTo(TriviaScore otherScore) {
        if (otherScore.getScore() == this.getScore()) {
            return otherScore.getUserId().compareTo(this.getUserId());
        } else {
            return Integer.compare(otherScore.getScore(), this.getScore());
        }
    }
}
