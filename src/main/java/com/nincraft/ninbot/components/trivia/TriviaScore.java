package com.nincraft.ninbot.components.trivia;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "TriviaScore")
@Data
public class TriviaScore {
    @Id
    @GeneratedValue
    @Column(name = "Id", nullable = false)
    private int id;
    @Column(name = "UserId", nullable = false)
    private String userId;
    @Column(name = "Score", nullable = false)
    private int score;
}
