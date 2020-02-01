package com.nincraft.ninbot.components.fun.pathogen;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PathogenUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String userId;
    private Integer infectionLevel = 0;
}
