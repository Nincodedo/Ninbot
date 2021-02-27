package dev.nincodedo.ninbot.components.ocw.gameserver;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class GameServer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String gameName;
    @Column(nullable = false)
    private String url;
    private String port;
}
