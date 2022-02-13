package dev.nincodedo.ninbot.components.pathogen.user;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class PathogenUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String serverId;
    private Integer infectionLevel = 0;
    private Date lastInitialInfection;
    private Date lastInitialCure;
    private Boolean vaccinated = false;
}
