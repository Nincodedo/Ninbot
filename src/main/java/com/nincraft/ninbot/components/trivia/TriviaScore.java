package com.nincraft.ninbot.components.trivia;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "TriviaScore")
@Data
public class TriviaScore implements Comparable {
    @Id
    @GeneratedValue
    @Column(name = "Id", nullable = false)
    private int id;
    @Column(name = "UserId", nullable = false)
    private String userId;
    @Column(name = "Score", nullable = false)
    private int score;

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
