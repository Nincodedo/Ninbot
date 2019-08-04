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
    private String discordId;
    private int turnipsOwned;
    private int bellsTotal;
}
