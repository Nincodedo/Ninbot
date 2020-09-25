package dev.nincodedo.ninbot.components.trivia;

import lombok.Data;

import javax.persistence.*;
import java.util.Comparator;

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
        return Comparator.comparing(TriviaScore::getScore)
                .thenComparing(TriviaScore::getUserId)
                .compare(this, otherScore);
    }
}
