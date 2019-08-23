package com.nincraft.ninbot.components.ac;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Villager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String discordId;
    @Column(nullable = false)
    private String discordServerId;
    private int turnipsOwned = 0;
    private int bellsTotal = 2000;


    public String getBellsTotalFormatted() {
        return String.format("%,d", getBellsTotal());
    }
}
