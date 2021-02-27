package dev.nincodedo.ninbot.components.ocw.gameserver;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String name;
}
